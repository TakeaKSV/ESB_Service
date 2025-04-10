package utd.ti.esb_service.model;

import java.util.Map;

public class GenericResponse {
    private String message;
    private Object data;
    private Map<String, Object> meta;
    
    // Getters y setters
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public Map<String, Object> getMeta() {
        return meta;
    }
    
    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }
}