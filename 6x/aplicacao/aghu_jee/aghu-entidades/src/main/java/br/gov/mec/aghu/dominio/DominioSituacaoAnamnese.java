package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioSituacaoAnamnese implements Dominio {
    /**
     * Em uso
     */
    U,
    /**
     * Livre
     */
    L;
    @Override
    public int getCodigo() {
      return this.ordinal();
    }
    @Override
    public String getDescricao() {
      switch (this) {
      case U:
        return "Em uso";
      case L:
        return "Livre";
      default:
        return "";
      }
    }
}