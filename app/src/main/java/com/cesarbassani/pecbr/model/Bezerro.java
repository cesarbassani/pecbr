package com.cesarbassani.pecbr.model;

import java.io.Serializable;

public class Bezerro implements Serializable {

    private String qtdeBezerrosPequenos;
    private String qtdeBezerrosMedios;
    private String qtdeBezerrosGrandes;

    public String getQtdeBezerrosPequenos() {
        return qtdeBezerrosPequenos;
    }

    public void setQtdeBezerrosPequenos(String qtdeBezerrosPequenos) {
        this.qtdeBezerrosPequenos = qtdeBezerrosPequenos;
    }

    public String getQtdeBezerrosMedios() {
        return qtdeBezerrosMedios;
    }

    public void setQtdeBezerrosMedios(String qtdeBezerrosMedios) {
        this.qtdeBezerrosMedios = qtdeBezerrosMedios;
    }

    public String getQtdeBezerrosGrandes() {
        return qtdeBezerrosGrandes;
    }

    public void setQtdeBezerrosGrandes(String qtdeBezerrosGrandes) {
        this.qtdeBezerrosGrandes = qtdeBezerrosGrandes;
    }
}
