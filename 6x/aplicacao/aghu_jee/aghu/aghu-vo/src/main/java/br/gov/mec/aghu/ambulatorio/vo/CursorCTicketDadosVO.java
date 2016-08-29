package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

public class CursorCTicketDadosVO implements Serializable {

	private static final long serialVersionUID = -5715775950772593601L;

	private Integer numero;
	
	private Date dtConsulta;
	
	//TO_CHAR(CON.DT_CONSULTA, 'dd/mm/yyyy')
	private String dtConsultaFormatado;
	//TO_CHAR(CON.DT_CONSULTA, 'hh24:mi')
	private String horaConsultaFormatado;
	
	private Integer serMatriculaConsultado;
	private Short serVinCodigoConsultado;
	private String descricaoCentroCustos;
	
	//DECODE(TO_CHAR(con.dt_consulta,'DY'),'SUN','DOM','MON','SEG','TUE','TER','WED','QUA','THU','QUI','FRI','SEX','SAT','SAB')
	private String diaSemanaFormatado;

	private String descricaoCondicaoAtendimento;
	private Boolean indExcedeProgramacao;
	
	//DECODE (con.ind_excede_programacao,'S','Aguarde, sua Consulta é EXTRA','                                   ')
	private String indExcedeProgramacaoFormatado;

	private String nomeReduzido;
	private String sigla;
	private Byte sala;
	
	//LPAD(VUSL.SALA,2,0)
	private String salaFormatado;
	
	private String nomeEquipe;
	private String retornoProcedureP2;
	private Integer pacCodigo;
	private Integer prontuario;
	private String nomePac;
	private Integer seq;
	
	private String user;
	
	private Integer gradePreSerMatricula;
	private Short gradePreSerVinCodigo;
	
	public void formatarDtConsulta(){
		if(this.dtConsulta != null){
			this.dtConsultaFormatado = DateUtil.obterDataFormatada(this.dtConsulta, "dd/MM/yyyy");
		}
	}
	
	public void formatarHoraConsulta(){
		if(this.dtConsulta != null){
			this.horaConsultaFormatado = DateUtil.obterDataFormatada(this.dtConsulta, "HH:mm");
		}
	}
	
	public void formatarDiaSemana(){
		if(this.dtConsulta != null){
			this.diaSemanaFormatado = DateUtil.obterDataFormatada(this.dtConsulta, "EEE").toUpperCase();
		}
	}
	
	public void formatarIndExcedeProgramacao(){
		
		if(this.indExcedeProgramacao != null){
			if(this.indExcedeProgramacao){
				this.indExcedeProgramacaoFormatado = "AGUARDE, SUA CONSULTA É EXTRA";
			}else{
				this.indExcedeProgramacaoFormatado = "                                   ";
			}
		}
	}
	
	public void formatarSala(){
		if(this.sala != null){
			this.salaFormatado = StringUtil.adicionaZerosAEsquerda(this.sala.toString(), 2);
		}
	}
	
	public void formatarRetornoProcedure(String value){
		if(value != null && !value.equals(" ")){
			this.retornoProcedureP2 = value;
		}else{
			this.retornoProcedureP2 = "EQUIPE";
		}
	}
	
	public void formatarCampos(){
		formatarDiaSemana();
		formatarDtConsulta();
		formatarHoraConsulta();
		formatarIndExcedeProgramacao();
		formatarSala();
	}
	
	public enum Fields {
		NUMERO("numero"),
		DT_CONSULTA("dtConsulta"),
		DT_CONSULTA_FORMATADO("dtConsultaFormatado"),
		HORA_CONSULTA_FORMATADO("horaConsultaFormatado"),
		SER_VIN_CODIGO_CONSULTADO("serVinCodigoConsultado"),
		SER_MATRICULA_CONSULTADO("serMatriculaConsultado"),
		DESCRICAO_CCT("descricaoCentroCustos"),
		DIA_SEMANA_FORMATADO("diaSemanaFormatado"),
		DESCRICAO_CAA("descricaoCondicaoAtendimento"),
		IND_EXCEDE_PROGRAMACAO("indExcedeProgramacao"),
		IND_EXCEDE_PROGRAMACAO_FORMATADO("indExcedeProgramacaoFormatado"),
		NOME_REDUZIDO("nomeReduzido"),
		SIGLA("sigla"),
		SALA("sala"),
		SALA_FORMATADO("salaFormatado"),
		NOME_EQP("nomeEquipe"),
		PAC_CODIGO("pacCodigo"),
		PRONTUARIO("prontuario"),
		NOME_PAC("nomePac"),
		SEQ("seq"),
		USER("user"),
		GRADE_SER_MATRICULA("gradePreSerMatricula"),
		GRADE_SER_VIN_CODIGO("gradePreSerVinCodigo"),
		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Date getDtConsulta() {
		return dtConsulta;
	}

	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}

	public String getDtConsultaFormatado() {
		return dtConsultaFormatado;
	}

	public void setDtConsultaFormatado(String dtConsultaFormatado) {
		this.dtConsultaFormatado = dtConsultaFormatado;
	}

	public String getHoraConsultaFormatado() {
		return horaConsultaFormatado;
	}

	public void setHoraConsultaFormatado(String horaConsultaFormatado) {
		this.horaConsultaFormatado = horaConsultaFormatado;
	}

	public Integer getSerMatriculaConsultado() {
		return serMatriculaConsultado;
	}

	public void setSerMatriculaConsultado(Integer serMatriculaConsultado) {
		this.serMatriculaConsultado = serMatriculaConsultado;
	}

	public Short getSerVinCodigoConsultado() {
		return serVinCodigoConsultado;
	}

	public void setSerVinCodigoConsultado(Short serVinCodigoConsultado) {
		this.serVinCodigoConsultado = serVinCodigoConsultado;
	}

	public String getDescricaoCentroCustos() {
		return descricaoCentroCustos;
	}

	public void setDescricaoCentroCustos(String descricaoCentroCustos) {
		this.descricaoCentroCustos = descricaoCentroCustos;
	}

	public String getDiaSemanaFormatado() {
		return diaSemanaFormatado;
	}

	public void setDiaSemanaFormatado(String diaSemanaFormatado) {
		this.diaSemanaFormatado = diaSemanaFormatado;
	}

	public String getDescricaoCondicaoAtendimento() {
		return descricaoCondicaoAtendimento;
	}

	public void setDescricaoCondicaoAtendimento(String descricaoCondicaoAtendimento) {
		this.descricaoCondicaoAtendimento = descricaoCondicaoAtendimento;
	}

	public Boolean getIndExcedeProgramacao() {
		return indExcedeProgramacao;
	}

	public void setIndExcedeProgramacao(Boolean indExcedeProgramacao) {
		this.indExcedeProgramacao = indExcedeProgramacao;
	}

	public String getIndExcedeProgramacaoFormatado() {
		return indExcedeProgramacaoFormatado;
	}

	public void setIndExcedeProgramacaoFormatado(
			String indExcedeProgramacaoFormatado) {
		this.indExcedeProgramacaoFormatado = indExcedeProgramacaoFormatado;
	}

	public String getNomeReduzido() {
		return nomeReduzido;
	}

	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Byte getSala() {
		return sala;
	}

	public void setSala(Byte sala) {
		this.sala = sala;
	}

	public String getSalaFormatado() {
		return salaFormatado;
	}

	public void setSalaFormatado(String salaFormatado) {
		this.salaFormatado = salaFormatado;
	}

	public String getNomeEquipe() {
		return nomeEquipe;
	}

	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}

	public String getRetornoProcedureP2() {
		return retornoProcedureP2;
	}

	public void setRetornoProcedureP2(String retornoProcedureP2) {
		this.retornoProcedureP2 = retornoProcedureP2;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getNomePac() {
		return nomePac;
	}

	public void setNomePac(String nomePac) {
		this.nomePac = nomePac;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getGradePreSerMatricula() {
		return gradePreSerMatricula;
	}

	public void setGradePreSerMatricula(Integer gradePreSerMatricula) {
		this.gradePreSerMatricula = gradePreSerMatricula;
	}

	public Short getGradePreSerVinCodigo() {
		return gradePreSerVinCodigo;
	}

	public void setGradePreSerVinCodigo(Short gradePreSerVinCodigo) {
		this.gradePreSerVinCodigo = gradePreSerVinCodigo;
	}
}