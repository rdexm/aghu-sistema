package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 
 * @author gabrielbigio
 * 
 */

public class FormularioDispAntimicrobianosVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8506276164398380870L;
	private String nomePaciente;
	private String reg;
	private int idade;
	private String setorLeito;
	private Date data;
	private String medico;
	
	private Boolean indEndocardite = Boolean.FALSE;
	private Boolean indPericardite = Boolean.FALSE;
	private Boolean indOutroCardiaco = Boolean.FALSE;
	private String outroCardiaco;
	
	private Boolean indSepse = Boolean.FALSE;
	private Boolean indCateteres = Boolean.FALSE;
	private Boolean indProteses = Boolean.FALSE;
	private Boolean indOutroCorrenteSanguinea = Boolean.FALSE;
	private String outroCorrenteSanguinea;
	
	private Boolean indEscara = Boolean.FALSE;
	private Boolean indCeluliteErisipela = Boolean.FALSE;
	private Boolean indQueimados = Boolean.FALSE;
	private Boolean indOutroDermatologico = Boolean.FALSE;
	private String outroDermatologico;
	
	private Boolean indArticular = Boolean.FALSE;
	private Boolean indOsseo = Boolean.FALSE;
	private Boolean indFraturas = Boolean.FALSE;
	private Boolean indOutroOsteoMuscular = Boolean.FALSE;
	private String outroOsteoMuscular;
	
	private Boolean indTRespSup = Boolean.FALSE;
	private Boolean indPneumonia = Boolean.FALSE;
	private Boolean indDpoc = Boolean.FALSE;
	private Boolean indOutroTratoRespiratorio = Boolean.FALSE;
	private String outroTratoRespiratorio;
	
	private Boolean indItuBaixa = Boolean.FALSE;
	private Boolean indItuAlta = Boolean.FALSE;
	private Boolean indPelvePerineo = Boolean.FALSE;
	private Boolean indOutroGenitoUrinario = Boolean.FALSE;
	private String outroGenitoUrinario;
	
	private Boolean indIncisionalSup = Boolean.FALSE;
	private Boolean indIncisionalProf = Boolean.FALSE;
	private Boolean indOrgaoCavid = Boolean.FALSE;
	
	private Boolean indEncefalite = Boolean.FALSE;
	private Boolean indMeningite = Boolean.FALSE;
	private Boolean indVentriculite = Boolean.FALSE;
	private Boolean indOutroNeurologico = Boolean.FALSE;
	private String outroNeurologico;
	
	private Boolean indSimInsuficienciaRenal = Boolean.FALSE;
	private Boolean indClearenceMenor10ml = Boolean.FALSE;
	private Boolean indClearenceEntre10e50ml = Boolean.FALSE;
	
	private Boolean indSimInsuficienciaHepatica = Boolean.FALSE;
	
	private Boolean indSimColetaCulturas = Boolean.FALSE;
	private Boolean indPontaCateter = Boolean.FALSE;
	private Boolean indHemocultura = Boolean.FALSE;
	private Boolean indUrina = Boolean.FALSE;
	private Boolean indSecrecoes = Boolean.FALSE;
	private Boolean indOutroColetaCulturas = Boolean.FALSE;
	private String outroColetaCulturas;
	
	private String justificativaSolicitacao;
	private String sugestoes;
	
	private List<ItemPrescricaoMedicaVO> listaAntimicrobianos;
	
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getReg() {
		return reg;
	}
	public void setReg(String reg) {
		this.reg = reg;
	}
	public int getIdade() {
		return idade;
	}
	public void setIdade(int idade) {
		this.idade = idade;
	}
	public String getSetorLeito() {
		return setorLeito;
	}
	public void setSetorLeito(String setorLeito) {
		this.setorLeito = setorLeito;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Boolean getIndEndocardite() {
		return indEndocardite;
	}
	public void setIndEndocardite(Boolean indEndocardite) {
		this.indEndocardite = indEndocardite;
	}
	public Boolean getIndPericardite() {
		return indPericardite;
	}
	public void setIndPericardite(Boolean indPericardite) {
		this.indPericardite = indPericardite;
	}
	public Boolean getIndOutroCardiaco() {
		return indOutroCardiaco;
	}
	public void setIndOutroCardiaco(Boolean indOutroCardiaco) {
		this.indOutroCardiaco = indOutroCardiaco;
	}
	public String getOutroCardiaco() {
		return outroCardiaco;
	}
	public void setOutroCardiaco(String outroCardiaco) {
		this.outroCardiaco = outroCardiaco;
	}
	public Boolean getIndSepse() {
		return indSepse;
	}
	public void setIndSepse(Boolean indSepse) {
		this.indSepse = indSepse;
	}
	public Boolean getIndCateteres() {
		return indCateteres;
	}
	public void setIndCateteres(Boolean indCateteres) {
		this.indCateteres = indCateteres;
	}
	public Boolean getIndProteses() {
		return indProteses;
	}
	public void setIndProteses(Boolean indProteses) {
		this.indProteses = indProteses;
	}
	public Boolean getIndOutroCorrenteSanguinea() {
		return indOutroCorrenteSanguinea;
	}
	public void setIndOutroCorrenteSanguinea(Boolean indOutroCorrenteSanguinea) {
		this.indOutroCorrenteSanguinea = indOutroCorrenteSanguinea;
	}
	public String getOutroCorrenteSanguinea() {
		return outroCorrenteSanguinea;
	}
	public void setOutroCorrenteSanguinea(String outroCorrenteSanguinea) {
		this.outroCorrenteSanguinea = outroCorrenteSanguinea;
	}
	public Boolean getIndEscara() {
		return indEscara;
	}
	public void setIndEscara(Boolean indEscara) {
		this.indEscara = indEscara;
	}
	public Boolean getIndCeluliteErisipela() {
		return indCeluliteErisipela;
	}
	public void setIndCeluliteErisipela(Boolean indCeluliteErisipela) {
		this.indCeluliteErisipela = indCeluliteErisipela;
	}
	public Boolean getIndQueimados() {
		return indQueimados;
	}
	public void setIndQueimados(Boolean indQueimados) {
		this.indQueimados = indQueimados;
	}
	public Boolean getIndOutroDermatologico() {
		return indOutroDermatologico;
	}
	public void setIndOutroDermatologico(Boolean indOutroDermatologico) {
		this.indOutroDermatologico = indOutroDermatologico;
	}
	public String getOutroDermatologico() {
		return outroDermatologico;
	}
	public void setOutroDermatologico(String outroDermatologico) {
		this.outroDermatologico = outroDermatologico;
	}
	public Boolean getIndArticular() {
		return indArticular;
	}
	public void setIndArticular(Boolean indArticular) {
		this.indArticular = indArticular;
	}
	public Boolean getIndOsseo() {
		return indOsseo;
	}
	public void setIndOsseo(Boolean indOsseo) {
		this.indOsseo = indOsseo;
	}
	public Boolean getIndFraturas() {
		return indFraturas;
	}
	public void setIndFraturas(Boolean indFraturas) {
		this.indFraturas = indFraturas;
	}
	public Boolean getIndOutroOsteoMuscular() {
		return indOutroOsteoMuscular;
	}
	public void setIndOutroOsteoMuscular(Boolean indOutroOsteoMuscular) {
		this.indOutroOsteoMuscular = indOutroOsteoMuscular;
	}
	public String getOutroOsteoMuscular() {
		return outroOsteoMuscular;
	}
	public void setOutroOsteoMuscular(String outroOsteoMuscular) {
		this.outroOsteoMuscular = outroOsteoMuscular;
	}
	public Boolean getIndTRespSup() {
		return indTRespSup;
	}
	public void setIndTRespSup(Boolean indTRespSup) {
		this.indTRespSup = indTRespSup;
	}
	public Boolean getIndPneumonia() {
		return indPneumonia;
	}
	public void setIndPneumonia(Boolean indPneumonia) {
		this.indPneumonia = indPneumonia;
	}
	public Boolean getIndDpoc() {
		return indDpoc;
	}
	public void setIndDpoc(Boolean indDpoc) {
		this.indDpoc = indDpoc;
	}
	public Boolean getIndOutroTratoRespiratorio() {
		return indOutroTratoRespiratorio;
	}
	public void setIndOutroTratoRespiratorio(Boolean indOutroTratoRespiratorio) {
		this.indOutroTratoRespiratorio = indOutroTratoRespiratorio;
	}
	public String getOutroTratoRespiratorio() {
		return outroTratoRespiratorio;
	}
	public void setOutroTratoRespiratorio(String outroTratoRespiratorio) {
		this.outroTratoRespiratorio = outroTratoRespiratorio;
	}
	public Boolean getIndItuBaixa() {
		return indItuBaixa;
	}
	public void setIndItuBaixa(Boolean indItuBaixa) {
		this.indItuBaixa = indItuBaixa;
	}
	public Boolean getIndItuAlta() {
		return indItuAlta;
	}
	public void setIndItuAlta(Boolean indItuAlta) {
		this.indItuAlta = indItuAlta;
	}
	public Boolean getIndPelvePerineo() {
		return indPelvePerineo;
	}
	public void setIndPelvePerineo(Boolean indPelvePerineo) {
		this.indPelvePerineo = indPelvePerineo;
	}
	public Boolean getIndOutroGenitoUrinario() {
		return indOutroGenitoUrinario;
	}
	public void setIndOutroGenitoUrinario(Boolean indOutroGenitoUrinario) {
		this.indOutroGenitoUrinario = indOutroGenitoUrinario;
	}
	public String getOutroGenitoUrinario() {
		return outroGenitoUrinario;
	}
	public void setOutroGenitoUrinario(String outroGenitoUrinario) {
		this.outroGenitoUrinario = outroGenitoUrinario;
	}
	public Boolean getIndIncisionalSup() {
		return indIncisionalSup;
	}
	public void setIndIncisionalSup(Boolean indIncisionalSup) {
		this.indIncisionalSup = indIncisionalSup;
	}
	public Boolean getIndIncisionalProf() {
		return indIncisionalProf;
	}
	public void setIndIncisionalProf(Boolean indIncisionalProf) {
		this.indIncisionalProf = indIncisionalProf;
	}
	public Boolean getIndOrgaoCavid() {
		return indOrgaoCavid;
	}
	public void setIndOrgaoCavid(Boolean indOrgaoCavid) {
		this.indOrgaoCavid = indOrgaoCavid;
	}
	public Boolean getIndEncefalite() {
		return indEncefalite;
	}
	public void setIndEncefalite(Boolean indEncefalite) {
		this.indEncefalite = indEncefalite;
	}
	public Boolean getIndMeningite() {
		return indMeningite;
	}
	public void setIndMeningite(Boolean indMeningite) {
		this.indMeningite = indMeningite;
	}
	public Boolean getIndVentriculite() {
		return indVentriculite;
	}
	public void setIndVentriculite(Boolean indVentriculite) {
		this.indVentriculite = indVentriculite;
	}
	public Boolean getIndOutroNeurologico() {
		return indOutroNeurologico;
	}
	public void setIndOutroNeurologico(Boolean indOutroNeurologico) {
		this.indOutroNeurologico = indOutroNeurologico;
	}
	public String getOutroNeurologico() {
		return outroNeurologico;
	}
	public void setOutroNeurologico(String outroNeurologico) {
		this.outroNeurologico = outroNeurologico;
	}
	public Boolean getIndSimInsuficienciaRenal() {
		return indSimInsuficienciaRenal;
	}
	public void setIndSimInsuficienciaRenal(Boolean indSimInsuficienciaRenal) {
		this.indSimInsuficienciaRenal = indSimInsuficienciaRenal;
	}
	public Boolean getIndNaoInsuficienciaRenal() {
		return !getIndSimInsuficienciaRenal();
	}
	public void setIndNaoInsuficienciaRenal(Boolean indNaoInsuficienciaRenal) {
		this.indSimInsuficienciaRenal = !indNaoInsuficienciaRenal;
	}
	public Boolean getIndClearenceMenor10ml() {
		return indClearenceMenor10ml;
	}
	public void setIndClearenceMenor10ml(Boolean indClearenceMenor10ml) {
		this.indClearenceMenor10ml = indClearenceMenor10ml;
	}
	public Boolean getIndClearenceEntre10e50ml() {
		return indClearenceEntre10e50ml;
	}
	public void setIndClearenceEntre10e50ml(Boolean indClearenceEntre10e50ml) {
		this.indClearenceEntre10e50ml = indClearenceEntre10e50ml;
	}
	public Boolean getIndSimInsuficienciaHepatica() {
		return indSimInsuficienciaHepatica;
	}
	public void setIndSimInsuficienciaHepatica(Boolean indSimInsuficienciaHepatica) {
		this.indSimInsuficienciaHepatica = indSimInsuficienciaHepatica;
	}
	public Boolean getIndNaoInsuficienciaHepatica() {
		return !getIndSimInsuficienciaHepatica();
	}
	public void setIndNaoInsuficienciaHepatica(Boolean indNaoInsuficienciaHepatica) {
		this.indSimInsuficienciaHepatica = !indNaoInsuficienciaHepatica;
	}
	public Boolean getIndSimColetaCulturas() {
		return indSimColetaCulturas;
	}
	public void setIndSimColetaCulturas(Boolean indSimColetaCulturas) {
		this.indSimColetaCulturas = indSimColetaCulturas;
	}
	public Boolean getIndNaoColetaCulturas() {
		return !getIndSimColetaCulturas();
	}
	public void setIndNaoColetaCulturas(Boolean indNaoColetaCulturas) {
		this.indSimColetaCulturas = !indNaoColetaCulturas;
	}
	public Boolean getIndPontaCateter() {
		return indPontaCateter;
	}
	public void setIndPontaCateter(Boolean indPontaCateter) {
		this.indPontaCateter = indPontaCateter;
	}
	public Boolean getIndHemocultura() {
		return indHemocultura;
	}
	public void setIndHemocultura(Boolean indHemocultura) {
		this.indHemocultura = indHemocultura;
	}
	public Boolean getIndUrina() {
		return indUrina;
	}
	public void setIndUrina(Boolean indUrina) {
		this.indUrina = indUrina;
	}
	public Boolean getIndSecrecoes() {
		return indSecrecoes;
	}
	public void setIndSecrecoes(Boolean indSecrecoes) {
		this.indSecrecoes = indSecrecoes;
	}
	public Boolean getIndOutroColetaCulturas() {
		return indOutroColetaCulturas;
	}
	public void setIndOutroColetaCulturas(Boolean indOutroColetaCulturas) {
		this.indOutroColetaCulturas = indOutroColetaCulturas;
	}
	public String getOutroColetaCulturas() {
		return outroColetaCulturas;
	}
	public void setOutroColetaCulturas(String outroColetaCulturas) {
		this.outroColetaCulturas = outroColetaCulturas;
	}
	public List<ItemPrescricaoMedicaVO> getListaAntimicrobianos() {
		return listaAntimicrobianos;
	}
	public void setListaAntimicrobianos(List<ItemPrescricaoMedicaVO> listaAntimicrobianos) {
		this.listaAntimicrobianos = listaAntimicrobianos;
	}
	public String getJustificativaSolicitacao() {
		return justificativaSolicitacao;
	}
	public void setJustificativaSolicitacao(String justificativaSolicitacao) {
		this.justificativaSolicitacao = justificativaSolicitacao;
	}
	public String getSugestoes() {
		return sugestoes;
	}
	public void setSugestoes(String sugestoes) {
		this.sugestoes = sugestoes;
	}
	public String getMedico() {
		return medico;
	}
	public void setMedico(String medico) {
		this.medico = medico;
	}
}
