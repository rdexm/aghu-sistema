package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;


public class MockPortalPlanejamentoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 8335343459483808043L;

	// #22454
	private Date dtAgenda; 
	private Integer pucSerMatricula;
	private Short pucSerVinCodigo;
	private Short pucUnfSeq;
	private DominioFuncaoProfissional pucIndFuncaoProf; // convertido no getter a partir de stringPucIndFuncaoProf 
	private Integer agdSeq;
	
	
	@Inject
	private RelatorioPortalPlanejamentoCirurgiasController relatorioPortalPlanejamentoCirurgiasController;
	
	public Date getDtAgenda() {
		return dtAgenda;
	}
	
	public void setDtAgenda(Date dtAgenda) {
		this.dtAgenda = dtAgenda;
	}
	
	public Integer getPucSerMatricula() {
		return pucSerMatricula;
	}
	
	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}
	
	public Short getPucSerVinCodigo() {
		return pucSerVinCodigo;
	}
	
	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}
	
	public Short getPucUnfSeq() {
		return pucUnfSeq;
	}
	
	public void setPucUnfSeq(Short pucUnfSeq) {
		this.pucUnfSeq = pucUnfSeq;
	}

	public DominioFuncaoProfissional getPucIndFuncaoProf() {
		return pucIndFuncaoProf;
	}

	public void setPucIndFuncaoProf(DominioFuncaoProfissional pucIndFuncaoProf) {
		this.pucIndFuncaoProf = pucIndFuncaoProf;
	}
	
	//22406
	private Short pUnfSeq;
	private Short pEspSeq;
	private String pEquipe;
	private Date pDtIni;
	private Date pDtFim;
	private Integer pPucSerMatricula;
	private Short pPucSerVinCodigo;
	private Short pPucUnfSeq;
	private DominioFuncaoProfissional pPucIndFuncaoProf;

	public Short getpUnfSeq() {
		return pUnfSeq;
	}

	public void setpUnfSeq(Short pUnfSeq) {
		this.pUnfSeq = pUnfSeq;
	}

	public Short getpEspSeq() {
		return pEspSeq;
	}

	public void setpEspSeq(Short pEspSeq) {
		this.pEspSeq = pEspSeq;
	}

	public String getpEquipe() {
		return pEquipe;
	}

	public void setpEquipe(String pEquipe) {
		this.pEquipe = pEquipe;
	}

	public Date getpDtIni() {
		return pDtIni;
	}

	public void setpDtIni(Date pDtIni) {
		this.pDtIni = pDtIni;
	}

	public Date getpDtFim() {
		return pDtFim;
	}

	public void setpDtFim(Date pDtFim) {
		this.pDtFim = pDtFim;
	}

	public Integer getpPucSerMatricula() {
		return pPucSerMatricula;
	}

	public void setpPucSerMatricula(Integer pPucSerMatricula) {
		this.pPucSerMatricula = pPucSerMatricula;
	}

	public Short getpPucSerVinCodigo() {
		return pPucSerVinCodigo;
	}

	public void setpPucSerVinCodigo(Short pPucSerVinCodigo) {
		this.pPucSerVinCodigo = pPucSerVinCodigo;
	}

	public Short getpPucUnfSeq() {
		return pPucUnfSeq;
	}

	public void setpPucUnfSeq(Short pPucUnfSeq) {
		this.pPucUnfSeq = pPucUnfSeq;
	}

	public DominioFuncaoProfissional getpPucIndFuncaoProf() {
		return pPucIndFuncaoProf;
	}
				   
	public void setpPucIndFuncaoProf(DominioFuncaoProfissional pPucIndFuncaoProf) {
		this.pPucIndFuncaoProf = pPucIndFuncaoProf;
	}
	
	public String preencheParam(){
		relatorioPortalPlanejamentoCirurgiasController.setpUnfSeq(pUnfSeq);
		relatorioPortalPlanejamentoCirurgiasController.setpEspSeq(pEspSeq);
		relatorioPortalPlanejamentoCirurgiasController.setpEquipe(pEquipe);
//		relatorioPortalPlanejamentoCirurgiasController.setpDtIni(String.valueOf(pDtIni.getTime()));//Utilizado dessa forma para manter o .page.xml sem alteração
//		relatorioPortalPlanejamentoCirurgiasController.setpDtFim(String.valueOf(pDtFim.getTime()));
		relatorioPortalPlanejamentoCirurgiasController.setpPucSerMatricula(pPucSerMatricula);
		relatorioPortalPlanejamentoCirurgiasController.setpPucSerVinCodigo(pPucSerVinCodigo);
		relatorioPortalPlanejamentoCirurgiasController.setpPucUnfSeq(pPucUnfSeq);
		relatorioPortalPlanejamentoCirurgiasController.setpPucIndFuncaoProf(pPucIndFuncaoProf.toString());
		relatorioPortalPlanejamentoCirurgiasController.setVoltarPara("/blococirurgico/portalplanejamento/mockPortalPlanejamento.xhtml");
		if(relatorioPortalPlanejamentoCirurgiasController.relatorioPossuiRegistros()){
			return "testarRelatorioPortalPlanejamentoCirurgias";
		}else{
			this.apresentarMsgNegocio(Severity.ERROR, "NAO_EXISTE_ESCALA_DE_SALA_PARA_PERIODO");
			return null;
		}
	}
	
	public void mockImpressaoPortalPlanejamento() throws JRException, SystemException, IOException{
		relatorioPortalPlanejamentoCirurgiasController.setpUnfSeq(pUnfSeq);
		relatorioPortalPlanejamentoCirurgiasController.setpEspSeq(pEspSeq);
		relatorioPortalPlanejamentoCirurgiasController.setpEquipe(pEquipe);
//		relatorioPortalPlanejamentoCirurgiasController.setpDtIni(String.valueOf(pDtIni.getTime()));//Utilizado dessa forma para manter o .page.xml sem alteração
//		relatorioPortalPlanejamentoCirurgiasController.setpDtFim(String.valueOf(pDtFim.getTime()));
		relatorioPortalPlanejamentoCirurgiasController.setpPucSerMatricula(pPucSerMatricula);
		relatorioPortalPlanejamentoCirurgiasController.setpPucSerVinCodigo(pPucSerVinCodigo);
		relatorioPortalPlanejamentoCirurgiasController.setpPucUnfSeq(pPucUnfSeq);
		relatorioPortalPlanejamentoCirurgiasController.setpPucIndFuncaoProf(pPucIndFuncaoProf.toString());
		relatorioPortalPlanejamentoCirurgiasController.setVoltarPara("/blococirurgico/portalplanejamento/mockPortalPlanejamento.xhtml");
		if(relatorioPortalPlanejamentoCirurgiasController.relatorioPossuiRegistros()){
			relatorioPortalPlanejamentoCirurgiasController.directPrint();
		}else{
			this.apresentarMsgNegocio(Severity.ERROR, "NAO_EXISTE_ESCALA_DE_SALA_PARA_PERIODO");
		}
	}
	
	//23393
	private AghUnidadesFuncionais unidade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(Object objPesquisa){
		String strPesquisa = (String) objPesquisa;
		return aghuFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoAndar(strPesquisa);
	}
	
	public String acessarEscalaDeSalas(){
		return "testarEscalaDeSalas";
	}

	public void setUnidade(AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}

	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}
	
	//#22328
	private Date dataAgenda;
	private Integer matriculaEquipe;
	private Short vinCodigoEquipe;
	private Short unfSeqEquipe;
	private String indFuncaoProfEquipe;
	private Short seqEspecialidade;
	private Short seqUnidFuncionalCirugica;
	private String cameFrom;
	private Long dataAgendaMili;
	private String situacaoAgenda;
	
	public String incluirEditarPlanej() {
		if(dataAgenda != null) {
			this.dataAgendaMili = dataAgenda.getTime();
		}
		return "testarIncluirEditarPlanej";
	}
	
//	#24924
	private Integer crgSeq;
	private Boolean edicao;
	
	public String detalharRegistroCirurgia() {
		return "detalharRegistroCirurgia";
	}
	
	public Long getDataAgendaMili() {
		return dataAgendaMili;
	}

	public void setDataAgendaMili(Long dataAgendaMili) {
		this.dataAgendaMili = dataAgendaMili;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public Integer getMatriculaEquipe() {
		return matriculaEquipe;
	}

	public void setMatriculaEquipe(Integer matriculaEquipe) {
		this.matriculaEquipe = matriculaEquipe;
	}

	public Short getVinCodigoEquipe() {
		return vinCodigoEquipe;
	}

	public void setVinCodigoEquipe(Short vinCodigoEquipe) {
		this.vinCodigoEquipe = vinCodigoEquipe;
	}

	public Short getUnfSeqEquipe() {
		return unfSeqEquipe;
	}

	public void setUnfSeqEquipe(Short unfSeqEquipe) {
		this.unfSeqEquipe = unfSeqEquipe;
	}

	public String getIndFuncaoProfEquipe() {
		return indFuncaoProfEquipe;
	}

	public void setIndFuncaoProfEquipe(String indFuncaoProfEquipe) {
		this.indFuncaoProfEquipe = indFuncaoProfEquipe;
	}

	public Short getSeqUnidFuncionalCirugica() {
		return seqUnidFuncionalCirugica;
	}

	public void setSeqUnidFuncionalCirugica(Short seqUnidFuncionalCirugica) {
		this.seqUnidFuncionalCirugica = seqUnidFuncionalCirugica;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Date getDataAgenda() {
		return dataAgenda;
	}

	public void setDataAgenda(Date dataAgenda) {
		this.dataAgenda = dataAgenda;
	}

	public Integer getAgdSeq() {
		return agdSeq;
	}

	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}

	public String getSituacaoAgenda() {
		return situacaoAgenda;
	}

	public void setSituacaoAgenda(String situacaoAgenda) {
		this.situacaoAgenda = situacaoAgenda;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	// #27417
	public String pesquisaAgendaCirurgia() {
		return "pesquisaAgendaCirurgia";
	}

}