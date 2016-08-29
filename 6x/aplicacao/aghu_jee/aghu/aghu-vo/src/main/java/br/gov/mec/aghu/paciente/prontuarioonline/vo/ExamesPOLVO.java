package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.model.AelDocResultadoExame;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelResultadoExame;

public class ExamesPOLVO {
	
	private Date dataHoraEventomaxExtratoItem;
	private Long qtdeNotaAdicional;
	private AelItemSolicitacaoExames aelItemSolicExame;
	private AelDocResultadoExame aelDocResultadoExames;
	private List<AelResultadoExame> resultadoExames;
	private AelMateriaisAnalises aelMaterialAnalise;
	private String descricaoMatAnalise;
	private Integer seqMatAnalise;
	
	
	public enum Fields {

			RESULTADO_EXAMES("resultadoExames"),
			DTHR_EVENTO_EXTRATO_ITEM("dataHoraEventomaxExtratoItem"),
			QTDE_NOTA_ADICIONAL("qtdeNotaAdicional"),
			AEL_ITEM_SOLIC_EXAME("aelItemSolicExame"),
			AEL_DOC_RESULTADO_EXAMES("aelDocResultadoExames"), 
			AEL_MATERIAL_ANALISE("aelMaterialAnalise"),
			DESCRICAO_MAT_ANALISE("descricaoMatAnalise"), 
			SEQ_MAT_ANALISE("seqMatAnalise");

			private String fields;

			private Fields(String fields) {
				this.fields = fields;
			}

			@Override
			public String toString() {
				return this.fields;
			}

		}


	public Date getDataHoraEventomaxExtratoItem() {
		return dataHoraEventomaxExtratoItem;
	}


	public Long getQtdeNotaAdicional() {
		return qtdeNotaAdicional;
	}


	public AelItemSolicitacaoExames getAelItemSolicExame() {
		return aelItemSolicExame;
	}


	public AelDocResultadoExame getAelDocResultadoExames() {
		return aelDocResultadoExames;
	}


	public List<AelResultadoExame> getResultadoExames() {
		return resultadoExames;
	}


	public AelMateriaisAnalises getAelMaterialAnalise() {
		return aelMaterialAnalise;
	}


	public void setDataHoraEventomaxExtratoItem(Date dataHoraEventomaxExtratoItem) {
		this.dataHoraEventomaxExtratoItem = dataHoraEventomaxExtratoItem;
	}


	public void setQtdeNotaAdicional(Long qtdeNotaAdicional) {
		this.qtdeNotaAdicional = qtdeNotaAdicional;
	}


	public void setAelItemSolicExame(AelItemSolicitacaoExames aelItemSolicExame) {
		this.aelItemSolicExame = aelItemSolicExame;
	}


	public void setAelDocResultadoExames(AelDocResultadoExame aelDocResultadoExames) {
		this.aelDocResultadoExames = aelDocResultadoExames;
	}


	public void setResultadoExames(List<AelResultadoExame> resultadoExames) {
		this.resultadoExames = resultadoExames;
	}


	public void setAelMaterialAnalise(AelMateriaisAnalises aelMaterialAnalise) {
		this.aelMaterialAnalise = aelMaterialAnalise;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((aelItemSolicExame == null) ? 0 : aelItemSolicExame
						.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExamesPOLVO other = (ExamesPOLVO) obj;
		if (aelItemSolicExame == null) {
			if (other.aelItemSolicExame != null) {
				return false;
			}
		} else if (!aelItemSolicExame.equals(other.aelItemSolicExame)) {
			return false;
		}
		return true;
	}


	public String getDescricaoMatAnalise() {
		return descricaoMatAnalise;
	}


	public void setDescricaoMatAnalise(String descricaoMatAnalise) {
		this.descricaoMatAnalise = descricaoMatAnalise;
	}


	public Integer getSeqMatAnalise() {
		return seqMatAnalise;
	}


	public void setSeqMatAnalise(Integer seqMatAnalise) {
		this.seqMatAnalise = seqMatAnalise;
	}
	
}
