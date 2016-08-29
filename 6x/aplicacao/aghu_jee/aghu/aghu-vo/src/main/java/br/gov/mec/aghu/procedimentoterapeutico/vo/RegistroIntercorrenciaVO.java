package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.core.commons.BaseBean;

public class RegistroIntercorrenciaVO implements BaseBean {

	private static final String D = "D";

	private static final long serialVersionUID = -8151265817850757153L;
	
	private String tipoIntercorrenciDescricao;
	private Short seqIntercorrencia;
	private String intercorrenciaDescricao;
	private Short ciclo;
	private Short dia;
	private Date dataInicio;
	private Integer cicloSeq;
	
	private String protocoloCicloDescricao;
	private String protocoloAssistencialTitulo;
	
	private String protocolo;
	
	private Integer seqSessao;
	private List<RegistroIntercorrenciaVO> listaItemSelecionado = new ArrayList<RegistroIntercorrenciaVO>();
	private boolean selecionado;
	
	private String diaD;
	
	private Short seqHorarioSessao;
	

	public String getTipoIntercorrenciDescricao() {
		return tipoIntercorrenciDescricao;
	}

	public void setTipoIntercorrenciDescricao(String tipoIntercorrenciDescricao) {
		this.tipoIntercorrenciDescricao = tipoIntercorrenciDescricao;
	}

	public String getIntercorrenciaDescricao() {
		return intercorrenciaDescricao;
	}

	public void setIntercorrenciaDescricao(String intercorrenciaDescricao) {
		this.intercorrenciaDescricao = intercorrenciaDescricao;
	}

	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public Short getDia() {
		return dia;
	}

	public void setDia(Short dia) {
		this.dia = dia;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Integer getCicloSeq() {
		return cicloSeq;
	}

	public void setCicloSeq(Integer cicloSeq) {
		this.cicloSeq = cicloSeq;
	}

	public enum Fields {

		DESCRICAO_TIPO_INTERCORRENCIA("tipoIntercorrenciDescricao"), 
		DESCRICAO_INTERCORRENCIA("IntercorrenciaDescricao"),
		CICLO("ciclo"),
		DIA("dia"),
		DATA_INICIO("dataInicio"),
		CICLO_SEQ("cicloSeq"),
		PROTOCOLO_DESCRICAO("protocoloCicloDescricao"),
		PROTOCOLO_TITULO("protocoloAssistencialTitulo"),
		SEQ_INTERCORRENCIA("seqIntercorrencia"),
		SEQ_SESSAO("seqSessao"),
		SEQ_HORARIO_SESSAO("seqHorarioSessao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public String getProtocoloCicloDescricao() {
		return protocoloCicloDescricao;
	}

	public void setProtocoloCicloDescricao(String protocoloCicloDescricao) {
		this.protocoloCicloDescricao = protocoloCicloDescricao;
	}

	public String getProtocoloAssistencialTitulo() {
		return protocoloAssistencialTitulo;
	}

	public void setProtocoloAssistencialTitulo(String protocoloAssistencialTitulo) {
		this.protocoloAssistencialTitulo = protocoloAssistencialTitulo;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public Short getSeqIntercorrencia() {
		return seqIntercorrencia;
	}

	public void setSeqIntercorrencia(Short seqIntercorrencia) {
		this.seqIntercorrencia = seqIntercorrencia;
	}

	public Integer getSeqSessao() {
		return seqSessao;
	}

	public void setSeqSessao(Integer seqSessao) {
		this.seqSessao = seqSessao;
	}

	public List<RegistroIntercorrenciaVO> getListaItemSelecionado() {
		return listaItemSelecionado;
	}

	public void setListaItemSelecionado(
			List<RegistroIntercorrenciaVO> listaItemSelecionado) {
		this.listaItemSelecionado = listaItemSelecionado;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public String getDiaD() {
		StringBuilder aux = new StringBuilder();
		if(this.dia != null){
			aux.append(D+dia);
		}
		diaD = aux.toString();
		return diaD;
	}
	
	public void setDiaD(String diaD) {
		this.diaD = diaD;
	}

	public Short getSeqHorarioSessao() {
		return seqHorarioSessao;
	}

	public void setSeqHorarioSessao(Short seqHorarioSessao) {
		this.seqHorarioSessao = seqHorarioSessao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((seqHorarioSessao == null) ? 0 : seqHorarioSessao.hashCode());
		result = prime * result
				+ ((seqSessao == null) ? 0 : seqSessao.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		RegistroIntercorrenciaVO other = (RegistroIntercorrenciaVO) obj;
		if (seqHorarioSessao == null) {
			if (other.seqHorarioSessao != null){
				return false;
			}
		} else if (!seqHorarioSessao.equals(other.seqHorarioSessao)){
			return false;
		}
		if (seqSessao == null) {
			if (other.seqSessao != null){
				return false;
			}
		} else if (!seqSessao.equals(other.seqSessao)){
			return false;
		}
		return true;
	}

}
