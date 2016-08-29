package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public class FichaPreOperatoriaVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7295144028720484566L;
	
	private Date dtCirurgia;				//1
	private String nome;					//2
	private String idade;					//3
	private String prontuario;			    //4
	private String unidade;					//5
	private String local; 					//6
	private Short nroAgenda;				//7
	private Date dthrPrevInicio;			//8
	
	//private String procedimentoList;		//10
	private List<LinhaReportVO> procedimentoList;		//10
	
	private List<LinhaReportVO> tricotomiaList; //12
	private String unidadeCirurgica;         //19

	public FichaPreOperatoriaVO() {
		
	}
	
	public FichaPreOperatoriaVO(String unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public enum Fields {
		
		NRO_AGENDA_ORDER("nroAgenda"),
		DTHR_PREV_INICIO_ORDER("dthrPrevInicio");

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
	
	public Date getDtCirurgia() {
		return dtCirurgia;
	}

	public void setDtCirurgia(Date dtCirurgia) {
		this.dtCirurgia = dtCirurgia;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getIdade() {
		return idade;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	
	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	
	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}
	

	public Short getNroAgenda() {
		return nroAgenda;
	}

	public void setNroAgenda(Short nroAgenda) {
		this.nroAgenda = nroAgenda;
	}


	public Date getDthrPrevInicio() {
		return dthrPrevInicio;
	}

	public void setDthrPrevInicio(Date dthrPrevInicio) {
		this.dthrPrevInicio = dthrPrevInicio;
	}	


	public void setProcedimentoList(List<LinhaReportVO> procedimentoList) {
		this.procedimentoList = procedimentoList;
	}

	public List<LinhaReportVO> getProcedimentoList() {
		return procedimentoList;
	}

	public List<LinhaReportVO> getTricotomiaList() {
		return tricotomiaList;
	}

	public void setTricotomiaList(List<LinhaReportVO> tricotomiaList) {
		this.tricotomiaList = tricotomiaList;
	}

	public String getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setUnidadeCirurgica(String unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}
}