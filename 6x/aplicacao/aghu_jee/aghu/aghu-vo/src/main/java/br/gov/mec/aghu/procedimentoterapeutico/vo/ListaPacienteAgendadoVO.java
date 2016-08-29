package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricaoSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ListaPacienteAgendadoVO implements Serializable {

	private static final long serialVersionUID = -1569979609171638487L;
	
	private Integer codigo;
	private Date data;
	private Date hora;
	private String paciente;
	private Integer codPaciente;
	private String controleFreqSituacao;
	private Date apacDataFim;
	private Long apacNumero;
	private String apacDataFimNumero;
	private Integer apacCount;
	private DominioSituacaoPrescricaoSessao situacaoLm;
	private String apacSemaforo;
	private Date dataNascimento;
	private Integer prontuario;
	private Date tempo;
	private String acomodacao;
	private Date chegada;
	private DominioSituacaoSessao situacaoSessao;
	private DominioSituacaoHorarioSessao situacaoHorario;
	private Integer ciclo;
	private Integer primeiraconsulta;
	private Integer indgmr;
	private List<MptProtocoloCiclo> listaProtocoloCiclo = new ArrayList<MptProtocoloCiclo>();
	private String colorColunaAmarelo;
	private String colorColunaVerde;
	private String colorLinha;
	private Short horarioSessaoSeq;
	private Short agendamentoSeq;
	private String mesToolTipRed;
	private String mesToolTipBlue;
	private String mesToolTipGreen;
	
	public enum Fields {
		CODIGO("codigo"),
		DATA("data"),
		HORA("hora"),
		PACIENTE("paciente"),
		COD_PACIENTE("codPaciente"),
		CONTROLE_FREQ_SITUACAO("controleFreqSituacao"),
		APAC_DATA_FIM_NUMERO("apacDataFimNumero"),
		APAC_DATA_FIM("apacDataFim"),
		APAC_DATA_NUMERO("apacNumero"),
		DATANASCIMENTO("dataNascimento"),
		PRONTUARIO("prontuario"),
		TEMPO("tempo"),
		ACOMODACAO("acomodacao"),
		CHEGADA("chegada"),
		SITUACAO_SESSAO("situacaoSessao"),
		CICLO("ciclo"),
		PRIMEIRA_CONSULTA("primeiraconsulta"),
		INDGMR("indgmr"),
		PROTOCOLO_CICLO("listaProtocoloCiclo"),
		SITUACAO_HORARIO("situacaoHorario"),
		COLOR_COLUNA_AMARELO("colorColunaAmarelo"),
		COLOR_COLUNA_VERDE("colorColunaVerde"),
		COLOR_LINHA("colorLinha"),
		SITUACAO_LM("situacaoLm"),
		AGENDAMENTO_SEQ("agendamentoSeq"),
		HORARIO_SESSAO_SEQ("horarioSessaoSeq");


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

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
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

	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}
	
	public String getAcomodacao() {
		return acomodacao;
	}

	public void setAcomodacao(String acomodacao) {
		this.acomodacao = acomodacao;
	}

	public Date getChegada() {
		return chegada;
	}

	public void setChegada(Date chegada) {
		this.chegada = chegada;
	}

	public Integer getCiclo() {
		return ciclo;
	}

	public void setCiclo(Integer ciclo) {
		this.ciclo = ciclo;
	}

	public Integer getPrimeiraconsulta() {
		return primeiraconsulta;
	}

	public void setPrimeiraconsulta(Integer primeiraconsulta) {
		this.primeiraconsulta = primeiraconsulta;
	}

	public Integer getIndgmr() {
		return indgmr;
	}

	public void setIndgmr(Integer indgmr) {
		this.indgmr = indgmr;
	}

	public List<MptProtocoloCiclo> getListaProtocoloCiclo() {
		return listaProtocoloCiclo;
	}

	public void setListaProtocoloCiclo(List<MptProtocoloCiclo> listaProtocoloCiclo) {
		this.listaProtocoloCiclo = listaProtocoloCiclo;
	}

	public DominioSituacaoSessao getSituacaoSessao() {
		return situacaoSessao;
	}

	public void setSituacaoSessao(DominioSituacaoSessao situacaoSessao) {
		this.situacaoSessao = situacaoSessao;
	}

	public DominioSituacaoHorarioSessao getSituacaoHorario() {
		return situacaoHorario;
	}

	public void setSituacaoHorario(DominioSituacaoHorarioSessao situacaoHorario) {
		this.situacaoHorario = situacaoHorario;
	}

	public String getColorColunaAmarelo() {
		return colorColunaAmarelo;
	}

	public void setColorColunaAmarelo(String colorColunaAmarelo) {
		this.colorColunaAmarelo = colorColunaAmarelo;
	}

	public String getColorColunaVerde() {
		return colorColunaVerde;
	}

	public void setColorColunaVerde(String colorColunaVerde) {
		this.colorColunaVerde = colorColunaVerde;
	}

	public String getColorLinha() {
		return colorLinha;
	}

	public void setColorLinha(String colorLinha) {
		this.colorLinha = colorLinha;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getCodPaciente() {
		return codPaciente;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public String getControleFreqSituacao() {
		return controleFreqSituacao;
	}

	public void setControleFreqSituacao(String controleFreqSituacao) {
		this.controleFreqSituacao = controleFreqSituacao;
	}

	public Date getApacDataFim() {
		return apacDataFim;
	}

	public void setApacDataFim(Date apacDataFim) {
		this.apacDataFim = apacDataFim;
	}

	public Long getApacNumero() {
		return apacNumero;
	}

	public void setApacNumero(Long apacNumero) {
		this.apacNumero = apacNumero;
	}

	public String getApacDataFimNumero() {
		return apacDataFimNumero;
	}
	
	public String getMesToolTipRed() {
		return mesToolTipRed;
	}

	public void setMesToolTipRed(String mesToolTipRed) {
		this.mesToolTipRed = mesToolTipRed;
	}

	public String getMesToolTipGreen() {
		return mesToolTipGreen;
	}

	public void setMesToolTipGreen(String mesToolTipGreen) {
		this.mesToolTipGreen = mesToolTipGreen;
	}

	public String getMesToolTipBlue() {
		return mesToolTipBlue;
	}

	public void setMesToolTipBlue(String mesToolTipBlue) {
		this.mesToolTipBlue = mesToolTipBlue;
	}

	public void setApacDataFimNumero(String apacDataFimNumero) throws ParseException {
		if(apacDataFimNumero != null){
			String dataFim = apacDataFimNumero.split("-")[1];
			//quebre a string inteira para pegar o nuemro da apac no primei indice
			this.apacNumero = Long.parseLong(apacDataFimNumero.split("-")[0]);
			//depois quebre novamente para pegar a data no segundo indice
			if(dataFim != null && !dataFim.isEmpty()){
				this.apacDataFim = DateUtil.obterData(Integer.parseInt(dataFim.split("/")[2]), Integer.parseInt(dataFim.split("/")[1]) - 1, Integer.parseInt(dataFim.split("/")[0]));
			}else{
				this.apacDataFim =null;
			}
			//Numero de linhas
			this.apacCount = Integer.parseInt(apacDataFimNumero.split("-")[2]);
		}
		this.apacDataFimNumero = apacDataFimNumero;
	}

	public Integer getApacCount() {
		return apacCount;
	}

	public void setApacCount(Integer apacCount) {
		this.apacCount = apacCount;
	}

	public String getApacSemaforo() {
		return apacSemaforo;
	}

	public void setApacSemaforo(String apacSemaforo) {
		this.apacSemaforo = apacSemaforo;
	}

	public DominioSituacaoPrescricaoSessao getSituacaoLm() {
		return situacaoLm;
	}

	public void setSituacaoLm(DominioSituacaoPrescricaoSessao situacaoLm) {
		this.situacaoLm = situacaoLm;
	}
	
	public Short getHorarioSessaoSeq() {
		return horarioSessaoSeq;
	}

	public void setHorarioSessaoSeq(Short horarioSessaoSeq) {
		this.horarioSessaoSeq = horarioSessaoSeq;
	}

	public Short getAgendamentoSeq() {
		return agendamentoSeq;
	}

	public void setAgendamentoSeq(Short agendamentoSeq) {
		this.agendamentoSeq = agendamentoSeq;
	}
	
}
