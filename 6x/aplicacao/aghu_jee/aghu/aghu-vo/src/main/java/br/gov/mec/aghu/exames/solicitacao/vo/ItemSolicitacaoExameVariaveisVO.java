package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoTransporte;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

/**
 * Classe que comporta as variáveis utilizadas nas procedures do FORMS relacionado a entidade AelItemSolicitacaoExames.
 * 
 *
 */

public class ItemSolicitacaoExameVariaveisVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9198242594132786573L;
	//Variaveis do forms
	private AelItemSolicitacaoExames itemSolicitacaoExame;
	private Float tempoColetas = new Float(0);
	private Boolean indSolicInformaColetas;
	private Boolean indGeraItemPorColetas;
	private Boolean indExigeDescMatAnls;
	private Date intervaloHorasAux;
	private Byte tempoAmostraDias = 0;
	private Byte tempoAmostraHoras = 0;
	private Date dthrProgramada;
	
	public void setItemSolicitacaoExame(AelItemSolicitacaoExames itemSolicitacaoExame) {
		this.itemSolicitacaoExame = itemSolicitacaoExame;
	}
	
//	public AelSolicitacaoExames getSolicitacaoExame() {
//		if(itemSolicitacaoExame != null) {
//			return itemSolicitacaoExame.getSolicitacaoExame();
//		}
//		return null;
//	}
	
	public Integer getSoeSeq() {
		if(itemSolicitacaoExame != null && itemSolicitacaoExame.getSolicitacaoExame() != null) {
			return itemSolicitacaoExame.getSolicitacaoExame().getSeq();
		}
		return null;
	}
	
	public AelExames getExame() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getExame();
		}
		return null;
	}
	
	public String getUfeEmaExaSigla() {
		if(itemSolicitacaoExame != null && itemSolicitacaoExame.getExame() != null) {
			return itemSolicitacaoExame.getExame().getSigla();
		}
		return null;
	}
	
	public AelMateriaisAnalises getMaterialAnalise() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getMaterialAnalise();
		}
		return null;
	}
	
	public Integer getUfeEmaManSeq() {
		if(itemSolicitacaoExame != null && itemSolicitacaoExame.getMaterialAnalise() != null) {
			return itemSolicitacaoExame.getMaterialAnalise().getSeq();
		}
		return null;
	}
	
	public AghUnidadesFuncionais getUnidadeFuncional() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getUnidadeFuncional();
		}
		return null;
	}
	
	public Short getUfeUnfSeq() {
		if(itemSolicitacaoExame != null && itemSolicitacaoExame.getUnidadeFuncional() != null) {
			return itemSolicitacaoExame.getUnidadeFuncional().getSeq();
		}
		return null;
	}
	
	public DominioTipoColeta getTipoColeta() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getTipoColeta();
		}
		return null;
	}
	
	public void setDthrProgramada(Date dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}
	
	public Date getDthrProgramada() {
		return dthrProgramada;
	}
	
	public AelSitItemSolicitacoes getSituacaoItemSolicitacao() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getSituacaoItemSolicitacao();
		}
		return null;
	}
	
//	public String getSitCodigo() {
//		if(itemSolicitacaoExame != null && itemSolicitacaoExame.getSituacaoItemSolicitacao() != null) {
//			return itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo();
//		}
//		return null;
//	}
	
	public Byte getNroAmostras() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getNroAmostras();
		}
		return null;
	}
	
	public Date getIntervaloHoras() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getIntervaloHoras();
		}
		return null;
	}
	
	public Byte getIntervaloDias() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getIntervaloDias();
		}
		return null;
	}
	
	public DominioTipoTransporte getTipoTransporte() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getTipoTransporte();
		}
		return null;
	}
	
	public Boolean getIndUsoO2() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getIndUsoO2();
		}
		return null;
	}
	
	public AelRegiaoAnatomica getRegiaoAnatomica() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getRegiaoAnatomica();
		}
		return null;
	}
	
	public Integer getRanSeq() {
		if(itemSolicitacaoExame != null && itemSolicitacaoExame.getRegiaoAnatomica() != null) {
			return itemSolicitacaoExame.getRegiaoAnatomica().getSeq();
		}
		return null;
	}
	
	public String getDescRegiaoAnatomica() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getDescRegiaoAnatomica();
		}
		return null;
	}
	
	public String getDescMaterialAnalise() {
		if(itemSolicitacaoExame != null) {
			return itemSolicitacaoExame.getDescMaterialAnalise();
		}
		return null;
	}
	
	public void setTempoColetas(Float tempoColetas) {
		this.tempoColetas = tempoColetas;
	}
	
	public Float getTempoColetas() {
		return tempoColetas;
	}
	
	public void setIndSolicInformaColetas(Boolean indSolicInformaColetas) {
		this.indSolicInformaColetas = indSolicInformaColetas;
	}
	
	public Boolean getIndSolicInformaColetas() {
		return indSolicInformaColetas;
	}
	
	public void setIndGeraItemPorColetas(Boolean indGeraItemPorColetas) {
		this.indGeraItemPorColetas = indGeraItemPorColetas;
	}
	
	public Boolean getIndGeraItemPorColetas() {
		return indGeraItemPorColetas;
	}
	
	public void setIndExigeDescMatAnls(Boolean indExigeDescMatAnls) {
		this.indExigeDescMatAnls = indExigeDescMatAnls;
	}
	
	public Boolean getIndExigeDescMatAnls() {
		return indExigeDescMatAnls;
	}
	
	public void setIntervaloHorasAux(Date intervaloHorasAux) {
		this.intervaloHorasAux = intervaloHorasAux;
	}
	
	public Date getIntervaloHorasAux() {
		return intervaloHorasAux;
	}
	
	public void setTempoAmostraDias(Byte tempoAmostraDias) {
		this.tempoAmostraDias = tempoAmostraDias;
	}
	
	public Byte getTempoAmostraDias() {
		return tempoAmostraDias;
	}
	
	public void setTempoAmostraHoras(Byte tempoAmostraHoras) {
		this.tempoAmostraHoras = tempoAmostraHoras;
	}
	
	public Byte getTempoAmostraHoras() {
		return tempoAmostraHoras;
	}
	
	
	
}
