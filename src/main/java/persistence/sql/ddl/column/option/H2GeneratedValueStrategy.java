package persistence.sql.ddl.column.option;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

public class H2GeneratedValueStrategy implements GeneratedValueStrategy {
    @Override
    public String generate(GeneratedValue generatedValue) {

        if (generatedValue != null && GenerationType.IDENTITY == generatedValue.strategy()) {
            return "auto_increment";
        }
        return "";
    }
}