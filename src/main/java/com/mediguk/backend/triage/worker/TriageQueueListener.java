package com.mediguk.backend.triage.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediguk.backend.triage.dto.request.TriageRequest;
import com.mediguk.backend.triage.dto.response.DemoStageOneResponse;
import com.mediguk.backend.triage.entity.TriageCase;
import com.mediguk.backend.triage.service.TriageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class TriageQueueListener implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final TriageService triageService;

    private static final String QUEUE_NAME = "mediguk:triage:queue";

    @Override
    public void run(String... args) {
        log.info("📡 Escuchando cola de triajes de Go en Redis: {}", QUEUE_NAME);

        // Hilo infinito para procesar la cola en segundo plano
        new Thread(() -> {
            while (true) {
                try {
                    // BRPOP bloquea el hilo hasta que llegue un mensaje (timeout 0 = infinito)
                    // Sacamos el último mensaje de la lista (el más viejo -> FIFO)
                    String json = redisTemplate.opsForList()
                                               .rightPop(QUEUE_NAME, Duration.ofSeconds(30));

                    if (json != null) {
                        log.info("📩 Nuevo triaje recibido de Go: {}", json);
                        
                        // Convertir JSON a Objeto Java
                        TriageRequest result = objectMapper.readValue(json, TriageRequest.class);

                        // Disparamos el motor de Triage (Stage 1: IA Especialista)
                        TriageCase entityProcesada = triageService.processTriage(result);

                        log.info("✅ Triaje guardado en DB para usuario: {}", result.id());
                        
                        // Creamos la respuesta para la Demo
                        DemoStageOneResponse response =
                            new DemoStageOneResponse(
                                entityProcesada.getId(),
                                entityProcesada.getCategory(),
                                entityProcesada.getStatus(),
                                entityProcesada.getFullTranscript(),
                                entityProcesada.getMedicalData() // Aquí va tu DermatologyDetails dentro del Map
                                );

                        System.out.println(response);
                    }
                } catch (Exception e) {
                    log.error("❌ Error procesando cola de Redis, reintentando en 5s...", e);
                    try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                }
            }
        }).start();
    }
}

