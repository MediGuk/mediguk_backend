package com.mediguk.backend.triage.model;

import java.util.Map;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * EL TRADUCTOR DE LA IA:
 * Este objeto es el resultado de parsear el JSON que escupe el VLM.
 */
@Builder
public record StageOneResult(
    // 1. CONTROL DE CALIDAD (Sincronizado con el JSON)
    @JsonProperty("confirmedCategory")
    String confirmedCategory,   
    
    @JsonProperty("isCategoryValid")
    boolean isCategoryValid,
    
    // private List<String> cleanedMedicalHistory;
    // private String cleanedPatientInput;  

    // 3. MOCHILA MEDICA (El mapa que contiene el SPECIALTYDETAILS record)
    @JsonProperty("details")
    Map<String, Object> rawMedicalData  
) {
    
    @SuppressWarnings("unchecked")
    public <T> T getDetail(String key) {
        return (T) rawMedicalData.get(key);
    }

    public boolean getBool(String key) {
        return Boolean.TRUE.equals(rawMedicalData.get(key));
    }
}


// {
//   "confirmedCategory": "DERMATOLOGIA",
//   "isCategoryValid": true,
//   "summary": "Paciente presenta una lesión cutánea eritematosa de bordes irregulares en el antebrazo izquierdo. No se observan signos de infección aguda, pero hay descamación periférica.",
//   "mainSymptom": "Prurito persistente (Picor)",
//   "details": {
//     "itching": true,
//     "lesionColor": "Rojizo violáceo",
//     "texture": "Rugosa con presencia de pápulas",
//     "evolution": "5 días desde la aparición inicial",
//     "anatomicalSite": "Antebrazo izquierdo",
//     "spread": false
//   }
// }


// {
//   "confirmedCategory": "RESPIRATORIO",
//   "isCategoryValid": true,
//   "summary": "Paciente con tos productiva y sibilancias...",
//   "mainSymptom": "Disnea leve",
//   "details": {
//     "coughType": "Productiva",
//     "expectorationColor": "Amarillenta",
//     "fever": true,
//     "breathingRate": "22 rpm"
//   }
// }

