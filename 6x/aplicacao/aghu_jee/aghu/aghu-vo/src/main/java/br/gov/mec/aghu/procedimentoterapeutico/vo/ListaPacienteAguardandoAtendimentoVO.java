package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoAdministracao;
import br.gov.mec.aghu.dominio.DominioSituacaoManipulacao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricaoSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.model.MptProtocoloCiclo;

public class ListaPacienteAguardandoAtendimentoVO extends ListaPacienteVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5081096021671896912L;
	
	private Date data;
	private Date hora;
	private Date horaChegada;
	private Date horaTriagem;
	private Integer codigoPaciente;
	private String paciente;
	private DominioSexo sexo;
	private Date dataNascimento;
	private Integer prontuario;
	private Short cicloNr;
	private Integer ciclo;
	private String prescritor;
	private DominioSituacaoSessao situacaoSessao;
	private DominioSituacaoAdministracao situacaoAdministracao;
	private Integer seqSessao;
	private Integer seqAtendimento;
	private Long primeiraconsulta;
	private Long indgmr;
	private String colorColunaAmarelo;
	private String colorLinhaAzul;
	private DominioSituacaoManipulacao manipulacao;
	private DominioSituacaoPrescricaoSessao situacaoLm;
	private List<MptProtocoloCiclo> listaProtocoloCiclo = new ArrayList<MptProtocoloCiclo>();
	private Integer pacCodigo;
	private List<RegistroIntercorrenciaVO> listaIntercorrenciasSelecionadas = new ArrayList<RegistroIntercorrenciaVO>();
	
	public ListaPacienteAguardandoAtendimentoVO(){
		
	}
	
	public ListaPacienteAguardandoAtendimentoVO (Date data, Date hora, Date horaChegada, 
												 Date horaTriagem, String paciente, DominioSexo sexo, Date dataNascimento,
												 Integer prontuario, Short cicloNr, Integer ciclo, String prescritor, 
												 DominioSituacaoSessao situacaoSessao, DominioSituacaoAdministracao situacaoAdministracao,
												 Integer seqSessao, Integer seqAtendimento, Long primeiraconsulta, Long indgmr, DominioSituacaoPrescricaoSessao situacaoLm, Integer pacCodigo, Integer codigoPaciente){
		this.data = data;
		this.hora = hora; 
		this.horaChegada = horaChegada;
		this.horaTriagem = horaTriagem;
		this.paciente = paciente;
		this.sexo = sexo;
		this.dataNascimento = dataNascimento;
		this.prontuario = prontuario;
		this.cicloNr = cicloNr;
		this.ciclo = ciclo;
		this.prescritor = prescritor;
		this.situacaoSessao = situacaoSessao;
		this.situacaoAdministracao = situacaoAdministracao;
		this.seqSessao = seqSessao;
		this.seqAtendimento = seqAtendimento;
		this.primeiraconsulta = primeiraconsulta;
		this.indgmr = indgmr;
		this.situacaoLm = situacaoLm;
		this.pacCodigo = pacCodigo;
		this.codigoPaciente = codigoPaciente;
	}

	public enum Fields {
		DATA("data"),
		HORA("hora"),
		HORA_CHEGADA("horaChegada"),
		HORA_TRIAGEM("horaTriagem"),
		CODIGO_PACIENTE("codigoPaciente"),
		PACIENTE("paciente"),
		SEXO("sexo"),
		DATA_NASCIMENTO("dataNascimento"),
		PRONTUARIO("prontuario"),
		CICLO_NR("cicloNr"),
		CICLO("ciclo"),
		PRESCRITOR("prescritor"),
		SITUACAO_SESSAO("situacaoSessao"),
		SITUACAO_ADMINISTRACAO("situacaoAdministracao"),
		SEQ_SESSAO("seqSessao"),		
		SEQ_ATENDIMENTO("seqAtendimento"),
		PRIMEIRA_CONSULTA("primeiraconsulta"),
		INDGMR("indgmr"),
		MANIPULACAO("manipulacao"),
		SITUACAO_LM("situacaoLm"),
		COLOR_COLUNA_AMARELO("colorColunaAmarelo"),		
		COLOR_LINHA_AZUL("colorLinhaAzul"),
		LISTA_PROTOCOLO_CICLO("listaProtocoloCiclo");


		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	
	public Date getData() {
		return data;
	}


	public void setData(Date data) {
		this.data = data;
	}


	public Date getHora() {
		return hora;
	}


	public void setHora(Date hora) {
		this.hora = hora;
	}


	public Date getHoraChegada() {
		return horaChegada;
	}


	public void setHoraChegada(Date horaChegada) {
		this.horaChegada = horaChegada;
	}


	public Date getHoraTriagem() {
		return horaTriagem;
	}


	public void setHoraTriagem(Date horaTriagem) {
		this.horaTriagem = horaTriagem;
	}


	public String getPaciente() {
		return paciente;
	}


	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}


	public DominioSexo getSexo() {
		return sexo;
	}


	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}


	public Date getDataNascimento() {
		return dataNascimento;
	}


	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}


	public Integer getProntuario() {
		return prontuario;
	}


	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public Short getCicloNr() {
		return cicloNr;
	}


	public void setCicloNr(Short cicloNr) {
		this.cicloNr = cicloNr;
	}


	public Integer getCiclo() {
		return ciclo;
	}


	public void setCiclo(Integer ciclo) {
		this.ciclo = ciclo;
	}


	public String getPrescritor() {
		return prescritor;
	}


	public void setPrescritor(String prescritor) {
		this.prescritor = prescritor;
	}


	public DominioSituacaoSessao getSituacaoSessao() {
		return situacaoSessao;
	}


	public void setSituacaoSessao(DominioSituacaoSessao situacaoSessao) {
		this.situacaoSessao = situacaoSessao;
	}


	public DominioSituacaoAdministracao getSituacaoAdministracao() {
		return situacaoAdministracao;
	}


	public void setSituacaoAdministracao(
			DominioSituacaoAdministracao situacaoAdministracao) {
		this.situacaoAdministracao = situacaoAdministracao;
	}


	public Integer getSeqSessao() {
		return seqSessao;
	}


	public void setSeqSessao(Integer seqSessao) {
		this.seqSessao = seqSessao;
	}


	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}


	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}


	public Long getPrimeiraconsulta() {
		return primeiraconsulta;
	}


	public void setPrimeiraconsulta(Long primeiraconsulta) {
		this.primeiraconsulta = primeiraconsulta;
	}


	public Long getIndgmr() {
		return indgmr;
	}


	public void setIndgmr(Long indgmr) {
		this.indgmr = indgmr;
	}


	public String getColorColunaAmarelo() {
		return colorColunaAmarelo;
	}


	public void setColorColunaAmarelo(String colorColunaAmarelo) {
		this.colorColunaAmarelo = colorColunaAmarelo;
	}


	public String getColorLinhaAzul() {
		return colorLinhaAzul;
	}


	public void setColorLinhaAzul(String colorLinhaAzul) {
		this.colorLinhaAzul = colorLinhaAzul;
	}
	
	public List<MptProtocoloCiclo> getListaProtocoloCiclo() {
		return listaProtocoloCiclo;
	}


	public void setListaProtocoloCiclo(List<MptProtocoloCiclo> listaProtocoloCiclo) {
		this.listaProtocoloCiclo = listaProtocoloCiclo;
	}


	public DominioSituacaoManipulacao getManipulacao() {
		return manipulacao;
	}


	public void setManipulacao(DominioSituacaoManipulacao manipulacao) {
		this.manipulacao = manipulacao;
	}


	public DominioSituacaoPrescricaoSessao getSituacaoLm() {
		return situacaoLm;
	}


	public void setSituacaoLm(DominioSituacaoPrescricaoSessao situacaoLm) {
		this.situacaoLm = situacaoLm;
	}
	
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}


	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}


	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}


	public List<RegistroIntercorrenciaVO> getListaIntercorrenciasSelecionadas() {
		return listaIntercorrenciasSelecionadas;
	}


	public void setListaIntercorrenciasSelecionadas(
			List<RegistroIntercorrenciaVO> listaIntercorrenciasSelecionadas) {
		this.listaIntercorrenciasSelecionadas = listaIntercorrenciasSelecionadas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codigoPaciente == null) ? 0 : codigoPaciente.hashCode());
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
		ListaPacienteAguardandoAtendimentoVO other = (ListaPacienteAguardandoAtendimentoVO) obj;
		if (codigoPaciente == null) {
			if (other.codigoPaciente != null){
				return false;
			}
		} else if (!codigoPaciente.equals(other.codigoPaciente)){
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
