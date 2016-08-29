package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.model.AelDocResultadoExamesHist;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelResultadosExamesHist;

public class ExamesHistoricoPOLVO {
	
	private Date dataHoraEventomaxExtratoItem;
	private Long qtdeNotaAdicional;
	private AelItemSolicExameHist aelItemSolicExameHist;
	private AelDocResultadoExamesHist aelDocResultadoExamesHist;
	private List<AelResultadosExamesHist> resultadoExames;
	private AelMateriaisAnalises aelMaterialAnalise;
	private String descricaoMatAnalise;
	private Integer seqMatAnalise;
	
	
	public enum Fields {

			RESULTADO_EXAMES("resultadoExames"),
			DTHR_EVENTO_EXTRATO_ITEM("dataHoraEventomaxExtratoItem"),
			QTDE_NOTA_ADICIONAL("qtdeNotaAdicional"),
			AEL_ITEM_SOLIC_EXAME_HIST("aelItemSolicExameHist"),
			AEL_DOC_RESULTADO_EXAMES_HIST("aelDocResultadoExamesHist"), 
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
	
	public List<AelResultadosExamesHist> getResultadoExames() {
		return resultadoExames;
	}

	public void setResultadoExames(List<AelResultadosExamesHist> resultadoExames) {
		this.resultadoExames = resultadoExames;
	}
	
	public Date getDataHoraEventomaxExtratoItem() {
		return dataHoraEventomaxExtratoItem;
	}

	public void setDataHoraEventomaxExtratoItem(Date dataHoraEventomaxExtratoItem) {
		this.dataHoraEventomaxExtratoItem = dataHoraEventomaxExtratoItem;
	}
	
	public Long getQtdeNotaAdicional() {
		return qtdeNotaAdicional;
	}

	public void setQtdeNotaAdicional(Long qtdeNotaAdicional) {
		this.qtdeNotaAdicional = qtdeNotaAdicional;
	}

	public AelItemSolicExameHist getAelItemSolicExameHist() {
		return aelItemSolicExameHist;
	}

	public AelDocResultadoExamesHist getAelDocResultadoExamesHist() {
		return aelDocResultadoExamesHist;
	}

	public void setAelItemSolicExameHist(AelItemSolicExameHist aelItemSolicExameHist) {
		this.aelItemSolicExameHist = aelItemSolicExameHist;
	}

	public void setAelDocResultadoExamesHist(
			AelDocResultadoExamesHist aelDocResultadoExamesHist) {
		this.aelDocResultadoExamesHist = aelDocResultadoExamesHist;
	}

	public AelMateriaisAnalises getAelMaterialAnalise() {
		return aelMaterialAnalise;
	}

	public void setAelMaterialAnalise(AelMateriaisAnalises aelMaterialAnalise) {
		this.aelMaterialAnalise = aelMaterialAnalise;
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