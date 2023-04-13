package symbol_Api_tasks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SymbolPojo {
    private String symbol;
    private String baseAsset;
    private String quoteAsset;
    private String openPrice;
    private String lowPrice;
    private String highPrice;
    private String lastPrice;
    private String volume;
    private String bidPrice;
    private String askPrice;
    private long at;
    }
//