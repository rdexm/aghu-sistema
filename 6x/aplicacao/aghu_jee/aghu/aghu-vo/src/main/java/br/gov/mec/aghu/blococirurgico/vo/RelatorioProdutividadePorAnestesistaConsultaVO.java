package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;

public class RelatorioProdutividadePorAnestesistaConsultaVO implements Serializable {

	private static final long serialVersionUID = -4799469192145613266L;

	private Integer ordem;
	private DominioFuncaoProfissional indFuncaoProf;
	private Short vinCodigo;
	private Integer matricula;
	private String nome;
	private Long quantidade;
	private Date dataInicioAnestesia;
	private Date dataFimAnestesia;
	private Short pucUnfSeq;
	private Short tamSeq;
	private String descricao;
	private Object qtdCirurgiaObject;
	private String espSigla;

	private Double qtdHoraDouble;
	private Long qtdHora;
	
	public RelatorioProdutividadePorAnestesistaConsultaVO() {
		
	}
	
	public enum Fields {

		ORDEM("ordem"),
		IND_FUNCAO_PROF("indFuncaoProf"),
		VIN_CODIGO("vinCodigo"),
		MATRICULA("matricula"),
		NOME("nome"),
		QTD_HORA("qtdHora"),
		QTD_HORA_DOUBLE("qtdHoraDouble"),
		QUANTIDADE("quantidade"),
		DATA_INICIO_ANESTESIA("dataInicioAnestesia"),
		DATA_FIM_ANESTESIA("dataFimAnestesia"),
		PUC_UNF_SEQ("pucUnfSeq"),
		TAM_SEQ("tamSeq"),
		DESCRICAO("descricao"),
		QTD_CIRURGIA_OBJECT("qtdCirurgiaObject"),
		ESP_SIGLA("espSigla");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	//Getters and Setters

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public DominioFuncaoProfissional getIndFuncaoProf() {
		return indFuncaoProf;
	}

	public void setIndFuncaoProf(DominioFuncaoProfissional indFuncaoProf) {
		this.indFuncaoProf = indFuncaoProf;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getQtdHora() {
		if(qtdHora == null && qtdHoraDouble != null){
			return getQtdHoraDouble().longValue();
		}
		return qtdHora;
	}

	public void setQtdHora(Long qtdHora) {
		this.qtdHora = qtdHora;
	}

	public void setDataInicioAnestesia(Date dataInicioAnestesia) {
		this.dataInicioAnestesia = dataInicioAnestesia;
	}

	public Date getDataInicioAnestesia() {
		return dataInicioAnestesia;
	}

	public void setDataFimAnestesia(Date dataFimAnestesia) {
		this.dataFimAnestesia = dataFimAnestesia;
	}

	public Date getDataFimAnestesia() {
		return dataFimAnestesia;
	}

	public void setPucUnfSeq(Short pucUnfSeq) {
		this.pucUnfSeq = pucUnfSeq;
	}

	public Short getPucUnfSeq() {
		return pucUnfSeq;
	}

	public void setTamSeq(Short tamSeq) {
		this.tamSeq = tamSeq;
	}

	public Short getTamSeq() {
		return tamSeq;
	}

	public void setQtdCirurgiaObject(Object qtdCirurgiaObject) {
		this.qtdCirurgiaObject = qtdCirurgiaObject;
	}

	public Object getQtdCirurgiaObject() {
		return qtdCirurgiaObject;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}

	public String getEspSigla() {
		return espSigla;
	}
	
	public Double getQtdHoraDouble() {
		return qtdHoraDouble;
	}

	public void setQtdHoraDouble(Double qtdHoraDouble) {
		this.qtdHoraDouble = qtdHoraDouble;
	}
}