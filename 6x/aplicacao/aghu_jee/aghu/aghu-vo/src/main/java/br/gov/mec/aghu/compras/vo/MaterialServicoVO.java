package br.gov.mec.aghu.compras.vo;

public class MaterialServicoVO {

    Integer codigoMatServ;
    String nomeMatServ;
    String descricaoMatServ;
    String unidadeMaterial;
    private Boolean estocavelMat;

    

    public Integer getCodigoMatServ() {
            return codigoMatServ;
    }

    public void setCodigoMatServ(Integer codigoMatServ) {
            this.codigoMatServ = codigoMatServ;
    }

    public String getNomeMatServ() {
            return nomeMatServ;
    }

    public void setNomeMatServ(String nomeMatServ) {
            this.nomeMatServ = nomeMatServ;
    }

    public String getDescricaoMatServ() {
            return descricaoMatServ;
    }

    public void setDescricaoMatServ(String descricaoMatServ) {
            this.descricaoMatServ = descricaoMatServ;
    }

    public String getUnidadeMaterial() {
            return unidadeMaterial;
    }

    public void setUnidadeMaterial(String unidadeMaterial) {
            this.unidadeMaterial = unidadeMaterial;
    }

    public Boolean getEstocavelMat() {
            return estocavelMat;
    }

    public void setEstocavelMat(Boolean estocavelMat) {
            this.estocavelMat = estocavelMat;
    }

}

