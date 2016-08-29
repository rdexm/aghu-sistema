package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.model.SigDirecionadores;

public class ObjetoCustoPesoClienteVO implements BaseBean {

	private static final long serialVersionUID = -7920491147304453088L;
	
	private String nome;
	private Integer ocvSeq;
	private Integer nroVersao;
	private Date dataInicio;
	private Date dataFim;
	private DominioSituacaoVersoesCustos indSituacao;
	private SigDirecionadores direcionador;
	private DominioSimNao possuiComposicao; 
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Integer getNroVersao() {
		return nroVersao;
	}
	
	public void setNroVersao(Integer nroVersao) {
		this.nroVersao = nroVersao;
	}
	
	public Date getDataInicio() {
		return dataInicio;
	}
	
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	
	public Date getDataFim() {
		return dataFim;
	}
	
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	public DominioSituacaoVersoesCustos getIndSituacao() {
		return indSituacao;
	}
	
	public void setIndSituacao(DominioSituacaoVersoesCustos indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}
	
	public DominioSimNao getPossuiComposicao() {
		return possuiComposicao;
	}

	public void setPossuiComposicao(DominioSimNao possuiComposicao) {
		this.possuiComposicao = possuiComposicao;
	}


	public SigDirecionadores getDirecionador() {
		return direcionador;
	}

	public void setDirecionador(SigDirecionadores direcionador) {
		this.direcionador = direcionador;
	}


	public enum Fields {
		NOME("nome"), 
		SEQ_OBJETO_CUSTO_VERSAO("ocvSeq"),
		NRO_VERSAO("nroVersao"), 
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim"),
		SITUACAO("indSituacao"),
		DIRECIONADOR("direcionador");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
