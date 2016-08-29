package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioLadoCirurgiaAgendas;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.MbcQuestao;
import br.gov.mec.aghu.model.MbcValorValidoCanc;

@SuppressWarnings({"ucd", "PMD.ExcessiveClassLength"})
public class CirurgiaVO implements Serializable {

	private static final long serialVersionUID = 7833434570403273023L;
	
	public final static String FOLHA_DUPLA_EVOLUCAO = "silk-paciente-prof-conectado";
	public final static String TITLE_FOLHA_DUPLA_EVOLUCAO = "Paciente com evolução feita pela categoria do profissional conectado.";
	
	public final static String FOLHA_VERMELHO_PREENCHIDA = "silk-cirurgia-descricao-pendente";
	public final static String TITLE_FOLHA_VERMELHO_PREENCHIDA = "Cirurgia/PDT com Descrição Pendente.";
	
	public final static String FOLHA_PREENCHIDA = "silk-cirurgia-com-descricao";
	public final static String TITLE_FOLHA_PREENCHIDA = "Cirurgia/PDT com Descrição.";
	
	public final static String FOLHA_DUPLA = "silk-cirurgia-mais-descricao";
	public final static String TITLE_FOLHA_DUPLA = "Cirurgia/PDT com mais de uma Descrição.";
	
	public final static String FOLHA_BRANCO = "silk-cirurgia-sem-descricao";
	public final static String TITLE_FOLHA_BRANCO = "Cirurgia/PDT sem Descrição.";
	
	public final static String PRANCHETA_VERMELHA = "silk-ficha-anestesia-pendente";
	public final static String TITLE_PRANCHETA_VERMELHA = "Ficha de Anestesia Pendente de conclusão.";

	public final static String PRANCHETA_BRANCA = "silk-ficha-anestesia-elaboracao";
	public final static String TITLE_PRANCHETA_BRANCA = "Ficha de Anestesia em Elaboração.";
	
	public final static String PRANCHETA_AMARELA = "silk-ficha-anestesia-concluida";
	public final static String TITLE_PRANCHETA_AMARELA = "Ficha de Anestesia Concluída.";
	
	public final static String PACIENTE_SEM_INT = "silk-cirurgia-paciente-nao-internado";
	public final static String TITLE_PACIENTE_SEM_INT = "Cirurgia/PDT de Internação e paciente não internado.";
	
	public final static String CHIP = "silk-certificacao-digital";
	public final static String TITLE_CHIP = "Certificação Digital.";
	
	public final static String REALIZADA = "silk-checked";
	public final static String TITLE_REALIZADA = "Cirurgia/PDT Realizada.";
	public final static String SORT_POR_REALIZADA = "A";
	
	public final static String MEDICO = "silk-paciente-transoperatorio";
	public final static String TITLE_MEDICO = "Paciente em Transoperatório.";	
	public final static String SORT_POR_MEDICO= "D";
	
	public final static String TELEFONE = "silk-telephone";
	public final static String TITLE_TELEFONE = "Paciente Chamado.";
	public final static String SORT_POR_TELEFONE = "B";
	
	public final static String AGENDA = "silk-agendado";
	public final static String TITLE_AGENDA = "Cirurgia/PDT Agendada.";
	public final static String SORT_POR_AGENDA = "E";
	
	public final static String PACIENTE_CAMA = "silk-leito";
	public final static String TITLE_PACIENTE_CAMA = "Paciente em Preparo.";
	public final static String SORT_POR_PACIENTE_CAMA = "C";
	
	public final static String CANCELADA = "silk-close-window";
	public final static String TILE_CANCELADA = "Cirurgia/PDT Cancelada.";
	public final static String SORT_POR_CANCELADA = "F";
	
	public final static String PROJ_PESQ = "silk-paciente-proj-pesquisa";
	public final static String TITLE_PROJ_PESQ = "Paciente vinculado a um projeto de pesquisa.";

	public static final String LARANJA = "orange";
	public final static String CERIH = "silk-leg-overbooking";
	public final static String TITLE_CERIH = "Exige CERIH.";
	
	public final static String GMR = "silk-leg-realizada";
	public final static String TITLE_GMR = "Paciente sinalizado portador de germe multirresistente.";
	
	public static final String AZUL = "#5595f6";
	public final static String SILK_BLUE = "silk-leg-cedido";
	public final static String TITLE_SILK_BLUE = "Paciente com cirurgia em outra data que não a corrente.";
	
	public static final String VERMELHO = "#FF0000"; //Fundo para paciente em transOperatório
	
	public CirurgiaVO() {
	}

	// CAMPOS PARA EDIÇÃO DA GRID
	private List<MotivoCancelamentoVO> listaMotivoCancelamentoVO;
	private MotivoCancelamentoVO motivoCancelamento;

	private MbcQuestao questao;
	private List<MbcValorValidoCanc> listaValorValidoCanc;
	private MbcValorValidoCanc valorValido;

	private String complementoCanc;

	// CAMPOS PARA A GRID
	private Integer crgSeq;
	private Date crgData;
	private String crgSala;
	private String crgSalaTruncado;
	private Date crgCriadoEm;
	private String crgProcPrinc;
	private String crgProcPrincTruncado;
	private String crgEquipe;
	private String crgEquipeTruncado;

	private Integer pacCodigo;
	private Integer prontuario;
	private String nomePaciente;
	private String nomePacienteTruncado;
	private Short espSeq;
	private String siglaEspecialidade;
	private String nomeEspecialidade;
	private DominioNaturezaFichaAnestesia naturezaAgenda;
	private DominioOrigemPacienteCirurgia origemPacienteCirurgia;
	private String leito;
	private Date dataInicioCirurgia;
	private Date dataFimCirurgia;
	private DominioLadoCirurgiaAgendas ladoCirurgia;
	private Short gridCspCnvCodigo;
	private Byte gridCspSeq;
	private DominioSituacaoCirurgia situacao;
	private Integer atdSeq;
	private Boolean digitaNotaSala;
	
	private Integer servidorMatricula;
	private Short servidorVinCodigo;

	private String desenho1;
	private String titleDesenho1;
	private String sortDesenho1;
	
	private String desenho2;
	private String titleDesenho2;
	
	private String desenho3;
	private String titleDesenho3;
	
	private String desenho4;
	private String titleDesenho4;
	
	private String desenho5;
	private String titleDesenho5;
	
	private String desenho6;
	private String titleDesenho6;
	
	private String corExibicao;
	private String corExibicaoColunaPaciente;
	
	private Short nroAgenda;

	// FLAGS DE CONTROLE DA TELA
	private boolean btSolicitar;
	private boolean btConsultar;
	private boolean btEvolucao;
	private boolean btLaudoAih;
	private boolean btImprimirPulseira;
	private boolean btAnotacoes;
	private boolean btImpPreAne;
	private boolean btAnestesia;

	private boolean vPermiteAnestesia;

	private Short mtcSeq;
	private Short vvcQesMtcSeq;
	private Integer vvcQesSeqp;
	private Short vvcSeqp;
	private Short qesMtcSeq;
	private Integer qesSeqp;
	
	private boolean outraDescricao;
	private boolean descrever;
	private boolean editarHabilitado;
	private boolean visualizarHabilitado;
	private boolean colunaAzul;
	private boolean colunaGmr;
	
	/*** CAMPOS NOVOS FLAVIO ***/
	private Boolean projetoPesquisaPaciente;	
	private Integer tempDescrCirPendente;
	private Integer tempDescrCir;
	private Integer tempDescrPdtPendente;
	private Integer tempDescrPdtSimples;
	private Long fichaSeq;
	private DominioIndPendenteAmbulatorio fichaPendente;
	private Integer temCertificacaoDigital;		
	private Integer temGermeMulti;	
	private Integer exigeCerih;	
	private Integer temEvolucao;	
	private String ltoLtoId;
	private Short pacUnfSeq;
	private Date dtUltInternacao;
	private Date dtUltAlta;
	private String qrtDescricao;
	private Integer temPacInternacao;
	private String informacaoQuartoLeito;
	private Byte pacUnfAndar;
	private String pacUnfAla;

	public enum Fields {

		CRG_SEQ("crgSeq"), CRG_DATA("crgData"), CRG_SALA("crgSala"),
		PRONTUARIO("prontuario"), 
		PAC_CODIGO("pacCodigo"), 
		NOME_PACIENTE("nomePaciente"), 
		ESP_SEQ("espSeq"), 
		SIGLA_ESPECIALIDADE("siglaEspecialidade"), 
		NOME_ESPECIALIDADE("nomeEspecialidade"), 
		NATUREZA_AGENDA("naturezaAgenda"), 
		DATA_FIM_CIRURGIA("dataFimCirurgia"), 
		DTHR_INICIO_CIRG("dataInicioCirurgia"),
		LADO_CIRURGIA("ladoCirurgia"),
		ORIGEM_PAC_CIRG("origemPacienteCirurgia"),
		CSP_CNV_CODIGO("gridCspCnvCodigo"),
		SITUACAO("situacao"),
		ATD_SEQ("atdSeq"),
		CRG_CRIADO_EM("crgCriadoEm"),
		QES_MTC_SEQ("qesMtcSeq"),
		QES_SEQP("qesSeqp"),
		VVC_QES_MTC_SEQ("vvcQesMtcSeq"),
		VVC_QES_SEQP("vvcQesSeqp"),
		VVC_SEQP("vvcSeqp"),
		MTC_SEQ("mtcSeq"),
		COMPLEMENTO_CANC("complementoCanc"),
		CSP_SEQ("gridCspSeq"),
		IND_DIGT_NOTA_SALA("digitaNotaSala"),
		SERVIDOR_MATRICULA("servidorMatricula"),
		SERVIDOR_VIN_CODIGO("servidorVinCodigo"),
		PROC_CIR_DESCRICAO("crgProcPrinc"),
		NOME_EQUIPE("crgEquipe"),
		PROJ_PESQ_PAC("projetoPesquisaPaciente"),		
		TEMP_DESC_CIR_PENDENTE("tempDescrCirPendente"),
		TEMP_DESCR_CIR("tempDescrCir"),
		TEMP_DESCR_PDT_PENDENTE("tempDescrPdtPendente"),
		TEMP_DESCR_PDT_SIMPLES("tempDescrPdtSimples"),
		FICHA_SEQ("fichaSeq"),
		FICHA_PENDENTE("fichaPendente"),
		TEM_CERTIF_DIGITAL("temCertificacaoDigital"),		
		TEM_GERME_MULTI("temGermeMulti"),
		EXIGE_CERIH("exigeCerih"),
		TEM_EVOLUCAO("temEvolucao"),
		TEM_PAC_INTERNACAO("temPacInternacao"),
        LTO_LTO_ID("ltoLtoId"),
        PAC_UNF_SEQ("pacUnfSeq"),
        DT_ULT_INTERNACAO("dtUltInternacao"),
        DT_ULT_ALTA("dtUltAlta"),
		QRT_DESCRICAO("qrtDescricao"),
		PAC_UNF_ANDAR("pacUnfAndar"),
		PAC_UNF_ALA("pacUnfAla");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public MotivoCancelamentoVO getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(MotivoCancelamentoVO motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public List<MotivoCancelamentoVO> getListaMotivoCancelamentoVO() {
		return listaMotivoCancelamentoVO;
	}

	public void setListaMotivoCancelamentoVO(
			List<MotivoCancelamentoVO> listaMotivoCancelamentoVO) {
		this.listaMotivoCancelamentoVO = listaMotivoCancelamentoVO;
	}

	public List<MbcValorValidoCanc> getListaValorValidoCanc() {
		return listaValorValidoCanc;
	}

	public void setListaValorValidoCanc(
			List<MbcValorValidoCanc> listaValorValidoCanc) {
		this.listaValorValidoCanc = listaValorValidoCanc;
	}

	public MbcQuestao getQuestao() {
		return questao;
	}

	public void setQuestao(MbcQuestao questao) {
		this.questao = questao;
	}

	public MbcValorValidoCanc getValorValido() {
		return valorValido;
	}

	public void setValorValido(MbcValorValidoCanc valorValido) {
		this.valorValido = valorValido;
	}

	public String getComplementoCanc() {
		return complementoCanc;
	}

	public void setComplementoCanc(String complementoCanc) {
		this.complementoCanc = complementoCanc;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Date getCrgData() {
		return crgData;
	}

	public void setCrgData(Date crgData) {
		this.crgData = crgData;
	}

	public String getCrgSala() {
		return crgSala;
	}

	public void setCrgSala(String crgSala) {
		this.crgSala = crgSala;
		this.crgSalaTruncado = crgSala !=null && crgSala.length() > 3 ? StringUtils.abbreviate(crgSala, 6) : null;
	}

	public String getCrgSalaTruncado() {
		return crgSalaTruncado;
	}

	public void setCrgSalaTruncado(String crgSalaTruncado) {
		this.crgSalaTruncado = crgSalaTruncado;
	}

	public Date getCrgCriadoEm() {
		return crgCriadoEm;
	}

	public void setCrgCriadoEm(Date crgCriadoEm) {
		this.crgCriadoEm = crgCriadoEm;
	}

	public String getCrgProcPrinc() {
		return crgProcPrinc;
	}

	public void setCrgProcPrinc(String crgProcPrinc) {
		this.crgProcPrinc = crgProcPrinc;
		this.crgProcPrincTruncado = crgProcPrinc != null && crgProcPrinc.length() > 20 ? StringUtils.abbreviate(crgProcPrinc, 23) : null;
	}

	public String getCrgProcPrincTruncado() {
		return crgProcPrincTruncado;
	}

	public void setCrgProcPrincTruncado(String crgProcPrincTruncado) {
		this.crgProcPrincTruncado = crgProcPrincTruncado;
	}

	public String getCrgEquipe() {
		return crgEquipe;
	}

	public void setCrgEquipe(String crgEquipe) {
		this.crgEquipe = crgEquipe;
		this.crgEquipeTruncado = crgEquipe != null && crgEquipe.length() > 10 ? StringUtils.abbreviate(crgEquipe, 13) : null;
	}

	public String getCrgEquipeTruncado() {
		return crgEquipeTruncado;
	}

	public void setCrgEquipeTruncado(String crgEquipeTruncado) {
		this.crgEquipeTruncado = crgEquipeTruncado;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
		this.nomePacienteTruncado = nomePaciente.length() > 15 ? StringUtils.abbreviate(nomePaciente, 18) : null;
	}

	public String getNomePacienteTruncado() {
		return nomePacienteTruncado;
	}

	public void setNomePacienteTruncado(String nomePacienteTruncado) {
		this.nomePacienteTruncado = nomePacienteTruncado;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public DominioNaturezaFichaAnestesia getNaturezaAgenda() {
		return naturezaAgenda;
	}

	public void setNaturezaAgenda(DominioNaturezaFichaAnestesia naturezaAgenda) {
		this.naturezaAgenda = naturezaAgenda;
	}

	public DominioOrigemPacienteCirurgia getOrigemPacienteCirurgia() {
		return origemPacienteCirurgia;
	}

	public void setOrigemPacienteCirurgia(
			DominioOrigemPacienteCirurgia origemPacienteCirurgia) {
		this.origemPacienteCirurgia = origemPacienteCirurgia;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Date getDataInicioCirurgia() {
		return dataInicioCirurgia;
	}

	public void setDataInicioCirurgia(Date dataInicioCirurgia) {
		this.dataInicioCirurgia = dataInicioCirurgia;
	}

	public Date getDataFimCirurgia() {
		return dataFimCirurgia;
	}

	public void setDataFimCirurgia(Date dataFimCirurgia) {
		this.dataFimCirurgia = dataFimCirurgia;
	}

	public Short getGridCspCnvCodigo() {
		return gridCspCnvCodigo;
	}

	public void setGridCspCnvCodigo(Short gridCspCnvCodigo) {
		this.gridCspCnvCodigo = gridCspCnvCodigo;
	}

	public DominioSituacaoCirurgia getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoCirurgia situacao) {
		this.situacao = situacao;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getDesenho1() {
		return desenho1;
	}

	public void setDesenho1(String desenho1) {
		this.desenho1 = desenho1;
	}

	public String getDesenho2() {
		return desenho2;
	}

	public void setDesenho2(String desenho2) {
		this.desenho2 = desenho2;
	}

	public String getDesenho3() {
		return desenho3;
	}

	public void setDesenho3(String desenho3) {
		this.desenho3 = desenho3;
	}

	public String getDesenho4() {
		return desenho4;
	}

	public void setDesenho4(String desenho4) {
		this.desenho4 = desenho4;
	}

	public String getDesenho5() {
		return desenho5;
	}

	public void setDesenho5(String desenho5) {
		this.desenho5 = desenho5;
	}

	public String getDesenho6() {
		return desenho6;
	}

	public void setDesenho6(String desenho6) {
		this.desenho6 = desenho6;
	}

	public String getCorExibicao() {
		return corExibicao;
	}

	public void setCorExibicao(String corExibicao) {
		this.corExibicao = corExibicao;
	}

	public boolean isBtSolicitar() {
		return btSolicitar;
	}

	public void setBtSolicitar(boolean btSolicitar) {
		this.btSolicitar = btSolicitar;
	}

	public boolean isBtConsultar() {
		return btConsultar;
	}

	public void setBtConsultar(boolean btConsultar) {
		this.btConsultar = btConsultar;
	}

	public boolean isBtEvolucao() {
		return btEvolucao;
	}

	public void setBtEvolucao(boolean btEvolucao) {
		this.btEvolucao = btEvolucao;
	}

	public boolean isBtLaudoAih() {
		return btLaudoAih;
	}

	public void setBtLaudoAih(boolean btLaudoAih) {
		this.btLaudoAih = btLaudoAih;
	}

	public boolean isBtImprimirPulseira() {
		return btImprimirPulseira;
	}

	public void setBtImprimirPulseira(boolean btImprimirPulseira) {
		this.btImprimirPulseira = btImprimirPulseira;
	}

	public boolean isBtAnotacoes() {
		return btAnotacoes;
	}

	public void setBtAnotacoes(boolean btAnotacoes) {
		this.btAnotacoes = btAnotacoes;
	}

	public boolean isBtImpPreAne() {
		return btImpPreAne;
	}

	public void setBtImpPreAne(boolean btImpPreAne) {
		this.btImpPreAne = btImpPreAne;
	}

	public boolean isBtAnestesia() {
		return btAnestesia;
	}

	public void setBtAnestesia(boolean btAnestesia) {
		this.btAnestesia = btAnestesia;
	}

	public boolean isvPermiteAnestesia() {
		return vPermiteAnestesia;
	}

	public void setvPermiteAnestesia(boolean vPermiteAnestesia) {
		this.vPermiteAnestesia = vPermiteAnestesia;
	}

	public Short getMtcSeq() {
		return mtcSeq;
	}


	public void setMtcSeq(Short mtcSeq) {
		this.mtcSeq = mtcSeq;
	}

	public Short getVvcQesMtcSeq() {
		return vvcQesMtcSeq;
	}

	public void setVvcQesMtcSeq(Short vvcQesMtcSeq) {
		this.vvcQesMtcSeq = vvcQesMtcSeq;
	}

	public Integer getVvcQesSeqp() {
		return vvcQesSeqp;
	}

	public void setVvcQesSeqp(Integer vvcQesSeqp) {
		this.vvcQesSeqp = vvcQesSeqp;
	}

	public Short getVvcSeqp() {
		return vvcSeqp;
	}

	public void setVvcSeqp(Short vvcSeqp) {
		this.vvcSeqp = vvcSeqp;
	}

	public Short getQesMtcSeq() {
		return qesMtcSeq;
	}

	public void setQesMtcSeq(Short qesMtcSeq) {
		this.qesMtcSeq = qesMtcSeq;
	}

	public Integer getQesSeqp() {
		return qesSeqp;
	}

	public void setQesSeqp(Integer qesSeqp) {
		this.qesSeqp = qesSeqp;
	}

	public Boolean getDigitaNotaSala() {
		return digitaNotaSala;
	}

	public void setDigitaNotaSala(Boolean digitaNotaSala) {
		this.digitaNotaSala = digitaNotaSala;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Byte getGridCspSeq() {
		return gridCspSeq;
	}

	public void setGridCspSeq(Byte gridCspSeq) {
		this.gridCspSeq = gridCspSeq;
	}

	public String getTitleDesenho1() {
		return titleDesenho1;
	}

	public void setTitleDesenho1(String titleDesenho1) {
		this.titleDesenho1 = titleDesenho1;
	}

	public String getTitleDesenho2() {
		return titleDesenho2;
	}

	public void setTitleDesenho2(String titleDesenho2) {
		this.titleDesenho2 = titleDesenho2;
	}

	public String getTitleDesenho3() {
		return titleDesenho3;
	}

	public void setTitleDesenho3(String titleDesenho3) {
		this.titleDesenho3 = titleDesenho3;
	}

	public String getTitleDesenho4() {
		return titleDesenho4;
	}

	public void setTitleDesenho4(String titleDesenho4) {
		this.titleDesenho4 = titleDesenho4;
	}

	public String getTitleDesenho5() {
		return titleDesenho5;
	}

	public void setTitleDesenho5(String titleDesenho5) {
		this.titleDesenho5 = titleDesenho5;
	}

	public String getTitleDesenho6() {
		return titleDesenho6;
	}

	public void setTitleDesenho6(String titleDesenho6) {
		this.titleDesenho6 = titleDesenho6;
	}

	public boolean isOutraDescricao() {
		return outraDescricao;
	}

	public void setOutraDescricao(boolean outraDescricao) {
		this.outraDescricao = outraDescricao;
	}

	public boolean isEditarHabilitado() {
		return editarHabilitado;
	}

	public void setEditarHabilitado(boolean editarHabilitado) {
		this.editarHabilitado = editarHabilitado;
	}

	public boolean isVisualizarHabilitado() {
		return visualizarHabilitado;
	}

	public void setVisualizarHabilitado(boolean visualizarHabilitado) {
		this.visualizarHabilitado = visualizarHabilitado;
	}

	public boolean isDescrever() {
		return descrever;
	}

	public void setDescrever(boolean descrever) {
		this.descrever = descrever;
	}

	public boolean isColunaAzul() {
		return colunaAzul;
	}

	public void setColunaAzul(boolean colunaAzul) {
		this.colunaAzul = colunaAzul;
	}

	public Integer getServidorMatricula() {
		return servidorMatricula;
	}

	public void setServidorMatricula(Integer servidorMatricula) {
		this.servidorMatricula = servidorMatricula;
	}

	public Short getServidorVinCodigo() {
		return servidorVinCodigo;
	}

	public void setServidorVinCodigo(Short servidorVinCodigo) {
		this.servidorVinCodigo = servidorVinCodigo;
	}

	public Short getNroAgenda() {
		return nroAgenda;
	}

	public void setNroAgenda(Short nroAgenda) {
		this.nroAgenda = nroAgenda;
	}
	
	public String getCorExibicaoColunaPaciente() {
		return corExibicaoColunaPaciente;
	}

	public void setCorExibicaoColunaPaciente(String corExibicaoColunaPaciente) {
		this.corExibicaoColunaPaciente = corExibicaoColunaPaciente;
	}

	public boolean isColunaGmr() {
		return colunaGmr;
	}

	public void setColunaGmr(boolean colunaGmr) {
		this.colunaGmr = colunaGmr;
	}
	
	
	public Boolean getProjetoPesquisaPaciente() {
		return projetoPesquisaPaciente;
	}

	public void setProjetoPesquisaPaciente(Boolean projetoPesquisaPaciente) {
		this.projetoPesquisaPaciente = projetoPesquisaPaciente;
	}

	public Integer getTempDescrCirPendente() {
		return tempDescrCirPendente;
	}

	public void setTempDescrCirPendente(Integer tempDescrCirPendente) {
		this.tempDescrCirPendente = tempDescrCirPendente;
	}

	public Integer getTempDescrCir() {
		return tempDescrCir;
	}

	public void setTempDescrCir(Integer tempDescrCir) {
		this.tempDescrCir = tempDescrCir;
	}

	public Integer getTempDescrPdtPendente() {
		return tempDescrPdtPendente;
	}

	public void setTempDescrPdtPendente(Integer tempDescrPdtPendente) {
		this.tempDescrPdtPendente = tempDescrPdtPendente;
	}

	public Integer getTempDescrPdtSimples() {
		return tempDescrPdtSimples;
	}

	public void setTempDescrPdtSimples(Integer tempDescrPdtSimples) {
		this.tempDescrPdtSimples = tempDescrPdtSimples;
	}

	public Long getFichaSeq() {
		return fichaSeq;
	}

	public void setFichaSeq(Long fichaSeq) {
		this.fichaSeq = fichaSeq;
	}

	public DominioIndPendenteAmbulatorio getFichaPendente() {
		return fichaPendente;
	}

	public void setFichaPendente(DominioIndPendenteAmbulatorio fichaPendente) {
		this.fichaPendente = fichaPendente;
	}
	
	public Integer getTemCertificacaoDigital() {
		return temCertificacaoDigital;
	}

	public void setTemCertificacaoDigital(Integer temCertificacaoDigital) {
		this.temCertificacaoDigital = temCertificacaoDigital;
	}

	public Integer getTemGermeMulti() {
		return temGermeMulti;
	}

	public void setTemGermeMulti(Integer temGermeMulti) {
		this.temGermeMulti = temGermeMulti;
	}	

	public Integer getExigeCerih() {
		return exigeCerih;
	}

	public void setExigeCerih(Integer exigeCerih) {
		this.exigeCerih = exigeCerih;
	}

		
	public Integer getTemEvolucao() {
		return temEvolucao;
	}

	public void setTemEvolucao(Integer temEvolucao) {
		this.temEvolucao = temEvolucao;
	}

	public Integer getTemPacInternacao() {
		return temPacInternacao;
	}

	public void setTemPacInternacao(Integer temPacInternacao) {
		this.temPacInternacao = temPacInternacao;
	}

	public DominioLadoCirurgiaAgendas getLadoCirurgia() {
		return ladoCirurgia;
	}

	public void setLadoCirurgia(DominioLadoCirurgiaAgendas ladoCirurgia) {
		this.ladoCirurgia = ladoCirurgia;
	}

	public String getSortDesenho1() {
		return sortDesenho1;
	}

	public void setSortDesenho1(String sortDesenho1) {
		this.sortDesenho1 = sortDesenho1;
	}
	
	public Object getCrgSalaOrdem(){
		if(NumberUtils.isNumber(this.crgSala)){
			return Integer.valueOf(this.crgSala);
		}else{
			return this.crgSala;			
		}
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public Short getPacUnfSeq() {
		return pacUnfSeq;
	}

	public void setPacUnfSeq(Short pacUnfSeq) {
		this.pacUnfSeq = pacUnfSeq;
	}

	public Date getDtUltInternacao() {
		return dtUltInternacao;
	}

	public void setDtUltInternacao(Date dtUltInternacao) {
		this.dtUltInternacao = dtUltInternacao;
	}

	public Date getDtUltAlta() {
		return dtUltAlta;
	}

	public void setDtUltAlta(Date dtUltAlta) {
		this.dtUltAlta = dtUltAlta;
	}

	public String getQrtDescricao() {
		return qrtDescricao;
	}

	public void setQrtDescricao(String qrtDescricao) {
		this.qrtDescricao = qrtDescricao;
	}

	public String getInformacaoQuartoLeito() {
		return informacaoQuartoLeito;
	}

	public void setInformacaoQuartoLeito(String informacaoQuartoLeito) {
		this.informacaoQuartoLeito = informacaoQuartoLeito;
	}

	public Byte getPacUnfAndar() {
		return pacUnfAndar;
	}

	public void setPacUnfAndar(Byte pacUnfAndar) {
		this.pacUnfAndar = pacUnfAndar;
	}

	public String getPacUnfAla() {
		return pacUnfAla;
	}

	public void setPacUnfAla(String pacUnfAla) {
		this.pacUnfAla = pacUnfAla;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crgSeq == null) ? 0 : crgSeq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj){ 
		if (this == obj){
			return true;
		}	
		if (obj == null){
			return false;
		}	
		if (!(obj instanceof CirurgiaVO)){
			return false;
		}	
		CirurgiaVO other = (CirurgiaVO) obj;
		if (crgSeq == null) {
			if (other.crgSeq != null){
				return false;
			}	
		} else if (!crgSeq.equals(other.crgSeq)){
			return false;
		}	
		return true;
	}
	
}
