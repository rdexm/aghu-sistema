package br.gov.mec.aghu.patrimonio.vo;

import br.gov.mec.aghu.dominio.DominioAceiteTecnico;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ImprimirFichaAceiteTecnicoBemPermanenteVO {

	
	private Integer receb;
	private Integer item;
	private Integer esl;
	private Integer nroAF;
	private Integer complementoAF;
	private Long notaFiscal;
	private String razaoSocial;
	private Long cpf;
	private Long cgc;
	private Integer matCodigo;
	private String matDescricao;
	private String marca;
	private String modelo;
	private String nomeTecnico;
	private Integer matricula;
	private String cargo;
	private Integer ccAreaTec;
	private String nomeCcAreaTec;
	private DominioAceiteTecnico indStatus;
	private String justificativa;
	@SuppressWarnings("unused")
	private String codigoDescricao;
	
	private String cpfCnpj;
	
	@SuppressWarnings("unused")
	private String descricaoStatus;
	
	public enum Fields {

		RECEB("receb"),
		ITEM("item"),
		ESL("esl"),
		NRO_AF("nroAF"),
		COMPLEMENTO_AF("complementoAF"),
		NOTA_FISCAL("notaFiscal"),
		RAZAO_SOCIAL("razaoSocial"),
		CPF("cpf"),
		CGC("cgc"),
		MAT_CODIGO("matCodigo"),
		MAT_DESCRICAO("matDescricao"),
		MARCA("marca"),
		MODELO("modelo"),
		NOME_TECNICO("nomeTecnico"),
		MATRICULA("matricula"),
		CARGO("cargo"),
		CC_AREA_TEC("ccAreaTec"),
		NOME_CC_AREA_TEC("nomeCcAreaTec"),
		IND_STATUS("indStatus"),
		JUSTIFICATIVA("justificativa");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	
	public String getCpfCnpj() {
		if (this.cpf != null) {
			cpfCnpj = CoreUtil.formataCPF(this.cpf).toString();
		} else if(this.cgc != null) {
			cpfCnpj = CoreUtil.formatarCNPJ(this.cgc).toString();
		}
		return cpfCnpj;
	}
	
	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}
	
	public Integer getReceb() {
		return receb;
	}

	public void setReceb(Integer receb) {
		this.receb = receb;
	}

	public Integer getItem() {
		return item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	public Integer getEsl() {
		return esl;
	}

	public void setEsl(Integer esl) {
		this.esl = esl;
	}

	public Integer getNroAF() {
		return nroAF;
	}

	public void setNroAF(Integer nroAF) {
		this.nroAF = nroAF;
	}

	public Integer getComplementoAF() {
		return complementoAF;
	}

	public void setComplementoAF(Integer complementoAF) {
		this.complementoAF = complementoAF;
	}

	public Long getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(Long notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Long getCgc() {
		return cgc;
	}

	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public String getMatDescricao() {
		return matDescricao;
	}

	public void setMatDescricao(String matDescricao) {
		this.matDescricao = matDescricao;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getNomeTecnico() {
		return nomeTecnico;
	}

	public void setNomeTecnico(String nomeTecnico) {
		this.nomeTecnico = nomeTecnico;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public Integer getCcAreaTec() {
		return ccAreaTec;
	}

	public void setCcAreaTec(Integer ccAreaTec) {
		this.ccAreaTec = ccAreaTec;
	}

	public String getNomeCcAreaTec() {
		return nomeCcAreaTec;
	}

	public void setNomeCcAreaTec(String nomeCcAreaTec) {
		this.nomeCcAreaTec = nomeCcAreaTec;
	}

	public DominioAceiteTecnico getIndStatus() {
		return indStatus;
	}

	public void setIndStatus(DominioAceiteTecnico indStatus) {
		this.indStatus = indStatus;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getDescricaoStatus() {
		if(indStatus != null){
			return indStatus.getDescricao();
		}else{
			return null;
		}
	}

	public void setDescricaoStatus(String descricaoStatus) {
		this.descricaoStatus = descricaoStatus;
	}

	public String getCodigoDescricao() {
		StringBuilder retorno = new StringBuilder(100);
		if(matCodigo != null){
			retorno.append(matCodigo);
			retorno.append(" / ");
		}
		if(matDescricao != null){
			retorno.append(matDescricao);
		}
		return retorno.toString();
	}

	public void setCodigoDescricao(String codigoDescricao) {
		this.codigoDescricao = codigoDescricao;
	}


}
