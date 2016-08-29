package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.Date;

public class ArquivoSecretariaVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4760862873271865927L;
	
	//SAIDA PARA CSV.
	private String nomePaciente; 		//Nome_Paciente
	private String dataNascimento;		//Data_Nascimento
	private Integer idade; 				//Idade
	private String endereco; 			//Endereco
	private String telefone; 			//Telefone
	private String dataResultado; 		//Data_Resultado
	private Integer solicitacao; 		//ise_soe_seq
	private Short item; 				//ise_seqp
	private String exame; 				//exame
	private String resultado; 			//Resultado
	private String gestante; 			//gestante
	
	//UTILIZADO NO HQL E ON/RN	
	private Integer pacCodigo; 			//pac.codigo
	private Integer rcdGtcSeq;			//ern.rcd_gtc_seq
	private Integer rcdSeqp; 			//ern.rcd_seqp
	private Long resultadoNumExp; 		//ern.resultado_num_exp
	private String resultadoAlfaNum; 	//ern.resultado_alfanum
	private Date dthrLiberada; 			//ise.dthr_liberada
	private String pclVelEmaExaSigla; 	//ree.pcl_vel_ema_exa_sigla
	private Integer pclVelEmaManSeq; 	//ree.pcl_vel_ema_man_seq
	private Integer pclVelSeqp; 		//ree.pcl_vel_seqp
	private Integer pclCalSeq; 			//ree.pcl_cal_seq
	private Integer pclSeqp; 			//ree.pcl_seqp
	private Integer seqp; 				//ree.seqp
	private Long valor; 				//ree.valor
	
	public ArquivoSecretariaVO() {
	}
	
	public ArquivoSecretariaVO(String nomeUsualMaterial, Integer soeSeq, Short iseSeqp, Integer rcdGtcSeq, Integer rcdSeqp, 
			Integer pacCodigo, Long resultadoNumExp, String resultadoAlfaNum, String pclVelEmaExaSigla, Integer pclVelEmaManSeq,
			Integer pclVelSeqp,Integer pclCalSeq,Integer pclSeqp,Integer seqp, Date dthrLiberada){
		this.exame = nomeUsualMaterial;
		this.solicitacao = soeSeq;
		this.item = iseSeqp;
		this.rcdGtcSeq = rcdGtcSeq;
		this.rcdSeqp = rcdSeqp;
		this.pacCodigo = pacCodigo;
		this.resultadoNumExp = resultadoNumExp;
		this.resultadoAlfaNum = resultadoAlfaNum;
		this.pclVelEmaExaSigla = pclVelEmaExaSigla; 
		this.pclVelEmaManSeq = pclVelEmaManSeq;
		this.pclVelSeqp = pclVelSeqp;
		this.pclCalSeq = pclCalSeq;
		this.pclSeqp = pclSeqp;
		this.seqp = seqp;
		this.dthrLiberada = dthrLiberada;
	}
	
	
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public Integer getIdade() {
		return idade;
	}
	public void setIdade(Integer idade) {
		this.idade = idade;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getDataResultado() {
		return dataResultado;
	}
	public void setDataResultado(String dataResultado) {
		this.dataResultado = dataResultado;
	}
	public Integer getSolicitacao() {
		return solicitacao;
	}
	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}
	public Short getItem() {
		return item;
	}
	public void setItem(Short item) {
		this.item = item;
	}
	public String getExame() {
		return exame;
	}
	public void setExame(String exame) {
		this.exame = exame;
	}
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	public String getGestante() {
		return gestante;
	}
	public void setGestante(String gestante) {
		this.gestante = gestante;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Integer getRcdGtcSeq() {
		return rcdGtcSeq;
	}
	public void setRcdGtcSeq(Integer rcdGtcSeq) {
		this.rcdGtcSeq = rcdGtcSeq;
	}
	public Integer getRcdSeqp() {
		return rcdSeqp;
	}
	public void setRcdSeqp(Integer rcdSeqp) {
		this.rcdSeqp = rcdSeqp;
	}
	public Long getResultadoNumExp() {
		return resultadoNumExp;
	}
	public void setResultadoNumExp(Long resultadoNumExp) {
		this.resultadoNumExp = resultadoNumExp;
	}
	public String getResultadoAlfaNum() {
		return resultadoAlfaNum;
	}
	public void setResultadoAlfaNum(String resultadoAlfaNum) {
		this.resultadoAlfaNum = resultadoAlfaNum;
	}
	public void setDthrLiberada(Date dthrLiberada) {
		this.dthrLiberada = dthrLiberada;
	}
	public Date getDthrLiberada() {
		return dthrLiberada;
	}
	public String getPclVelEmaExaSigla() {
		return pclVelEmaExaSigla;
	}
	public void setPclVelEmaExaSigla(String pclVelEmaExaSigla) {
		this.pclVelEmaExaSigla = pclVelEmaExaSigla;
	}
	public Integer getPclVelEmaManSeq() {
		return pclVelEmaManSeq;
	}
	public void setPclVelEmaManSeq(Integer pclVelEmaManSeq) {
		this.pclVelEmaManSeq = pclVelEmaManSeq;
	}
	public Integer getPclVelSeqp() {
		return pclVelSeqp;
	}
	public void setPclVelSeqp(Integer pclVelSeqp) {
		this.pclVelSeqp = pclVelSeqp;
	}
	public Integer getPclCalSeq() {
		return pclCalSeq;
	}
	public void setPclCalSeq(Integer pclCalSeq) {
		this.pclCalSeq = pclCalSeq;
	}
	public Integer getPclSeqp() {
		return pclSeqp;
	}
	public void setPclSeqp(Integer pclSeqp) {
		this.pclSeqp = pclSeqp;
	}
	public Integer getSeqp() {
		return seqp;
	}
	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}
	public Long getValor() {
		return valor;
	}
	public void setValor(Long valor) {
		this.valor = valor;
	}

	public enum Fields {
		NOME_PACIENTE("nomePaciente"),
		DATA_NASCIMENTO("dataNascimento"),
		IDADE("idade"),
		ENDERECO("endereco"),
		TELEFONE("telefone"),
		DATA_RESULTADO("dataResultado"),
		SOLICITACAO("solicitacao"),
		ITEM("item"),
		EXAME("exame"),
		RESULTADO("resultado"),
		GESTANTE("gestante"),
		DATA_HORA_LIBERADA("dthrLiberada"),
		PAC_CODIGO("pacCodigo"),
		PCL_VEL_EMA_EXA_SIGLA("pclVelEmaExaSigla"),
		PCL_VEL_EMA_MAN_SEQ("pclVelEmaManSeq"),
		PCL_VEL_SEQP("pclVelSeqp"),
		PCL_CAL_SEQ("pclCalSeq"),
		PCL_SEQP("pclSeqp"),
		RCD_GTC_SEQ("rcdGtcSeq"),
		SEQP("seqp"),
		RCD_SEQP("rcdSeqp"),
		VALOR("valor"),
		RESULTADO_NUM_EXP("resultadoNumExp"),
		RESULTADO_ALFA_NUM("resultadoAlfaNum");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}	
}
