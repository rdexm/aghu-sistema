package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastrarProfissionaisUnidCirurgicaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 this.setProfissionalUnid(new MbcProfAtuaUnidCirgs());
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3288102063913966552L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	private AghUnidadesFuncionais unidadeFuncional;
	private RapServidores profissionalServ;
	private RapServidores profissionalProfessor;
	private DominioFuncaoProfissional funcaoProfissional;//dominioFuncaoProfissionalItens
	private DominioSituacao situacaoProfissional;
	private Boolean ativo;
	private Boolean desabilitarCampos = false;
	
	
	private MbcProfAtuaUnidCirgs profissionalUnid;
	
	private final String PAGE_LIST_PROF_UNI_CIR = "pesquisaProfissionaisUnidCirurgica";
	
	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
	 

	 

		if(profissionalUnid!=null && profissionalUnid.getId()!=null){
			this.unidadeFuncional = profissionalUnid.getUnidadeFuncional();
			this.profissionalServ = profissionalUnid.getRapServidores();
			this.profissionalProfessor = (profissionalUnid.getMbcProfAtuaUnidCirgs()!=null) ? profissionalUnid.getMbcProfAtuaUnidCirgs().getRapServidores() :null;
			this.funcaoProfissional = profissionalUnid.getId().getIndFuncaoProf();
			this.situacaoProfissional = profissionalUnid.getSituacao();
			ativo = profissionalUnid.getSituacao().isAtivo();
			desabilitarCampos = true;
		}else{
			ativo = true;
			this.unidadeFuncional = null;
			this.profissionalServ = null;
			this.profissionalProfessor = null;
			this.funcaoProfissional =null;
			desabilitarCampos = false;
		}
	
	}
	

	public boolean isAtiva(MbcUnidadeNotaSala unidSala){
		return unidSala.getSituacao().isAtivo();
	}
	
	public String confirmar(){
		try {

			boolean inclusao = profissionalUnid.getId()==null; 

			if(inclusao){
				profissionalUnid.setUnidadeFuncional(unidadeFuncional);
				profissionalUnid.setIndFuncaoProf(funcaoProfissional);
				profissionalUnid.setRapServidores(profissionalServ);
				profissionalUnid.setServidorCadastrado(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), new Date()));
			}else{
				profissionalUnid.setServidorAlteradoPor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), new Date()));
			}
			
			if(profissionalProfessor != null){
				MbcProfAtuaUnidCirgs puc = new MbcProfAtuaUnidCirgs();
				MbcProfAtuaUnidCirgsId pucId = new MbcProfAtuaUnidCirgsId();

				pucId.setIndFuncaoProf(DominioFuncaoProfissional.MPF);
				pucId.setSerMatricula(profissionalProfessor.getId().getMatricula());
				pucId.setSerVinCodigo(profissionalProfessor.getId().getVinCodigo());
				pucId.setUnfSeq(unidadeFuncional.getSeq());
				puc.setId(pucId);

				profissionalUnid.setMbcProfAtuaUnidCirgs(puc);
			}else{
				profissionalUnid.setMbcProfAtuaUnidCirgs(null);
			}

			profissionalUnid.setSituacao(DominioSituacao.getInstance(ativo));
			this.blocoCirurgicoCadastroApoioFacade.persistirProfiUnidade(profissionalUnid);

			if(inclusao){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_PROFISSIONAL_UNIDADE");
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_PROFISSIONAL_UNIDADE");
			}

			return cancelar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
		return null;
		
	}

	public String cancelar(){
		this.setProfissionalUnid(new MbcProfAtuaUnidCirgs());
		return PAGE_LIST_PROF_UNI_CIR;
	}

	public List<AghUnidadesFuncionais> obterUnidadeFuncional(String filtro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(filtro),obterUnidadeFuncionalCount(filtro));
	}
	
	public Long obterUnidadeFuncionalCount(String filtro) {
        return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(filtro);
    }

	// Metódo para Suggestion Box de profissional
	public List<RapServidores> obterProfissionalUnidCirur(String objPesquisa){
		return this.returnSGWithCount(registroColaboradorFacade.pesquisarServidoresAtivosPendentes(objPesquisa),obterProfissionalUnidCirurCount(objPesquisa));
	}

	public Long obterProfissionalUnidCirurCount(String objPesquisa){
		return registroColaboradorFacade.pesquisarServidoresAtivosPendentesCount(objPesquisa);
	}
	
	public List<RapServidores> obterProfissionalMedicoProfessor(String objPesquisa){
		if(unidadeFuncional!=null && unidadeFuncional.getSeq()!=null){
			return this.returnSGWithCount(registroColaboradorFacade.pesquisarServidoresAtivosPendentesMedicoProfessoreUnidade(objPesquisa, unidadeFuncional.getSeq()),obterProfissionalMedicoProfessorCount(objPesquisa));
		}else{
			return null;
		}
	}
	
    public Long obterProfissionalMedicoProfessorCount(String objPesquisa){
        if(unidadeFuncional!=null && unidadeFuncional.getSeq()!=null){
                return registroColaboradorFacade.pesquisarServidoresAtivosPendentesMedicoProfessoreUnidadeCount(objPesquisa, unidadeFuncional.getSeq());
        }else{
                return null;
        }
    }

	/*
	 * Getters and Setters abaixo...
	 */
	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setComprasCadastrosBasicosFacade(
			IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public RapServidores getProfissionalServ() {
		return profissionalServ;
	}

	public void setProfissionalServ(RapServidores profissionalServ) {
		this.profissionalServ = profissionalServ;
	}

	public DominioFuncaoProfissional getFuncaoProfissional() {
		return funcaoProfissional;
	}

	public void setFuncaoProfissional(DominioFuncaoProfissional funcaoProfissional) {
		this.funcaoProfissional = funcaoProfissional;
	}

	public DominioSituacao getSituacaoProfissional() {
		return situacaoProfissional;
	}

	public void setSituacaoProfissional(DominioSituacao situacaoProfissional) {
		this.situacaoProfissional = situacaoProfissional;
	}

	public MbcProfAtuaUnidCirgs getProfissionalUnid() {
		return profissionalUnid;
	}

	public void setProfissionalUnid(MbcProfAtuaUnidCirgs profissionalUnid) {
		this.profissionalUnid = profissionalUnid;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getDesabilitarCampos() {
		return desabilitarCampos;
	}

	public void setDesabilitarCampos(Boolean desabilitarCampos) {
		this.desabilitarCampos = desabilitarCampos;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public RapServidores getProfissionalProfessor() {
		return profissionalProfessor;
	}

	public void setProfissionalProfessor(RapServidores profissionalProfessor) {
		this.profissionalProfessor = profissionalProfessor;
	}
}