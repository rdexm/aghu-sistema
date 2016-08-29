package br.gov.mec.aghu.blococirurgico.cedenciasala.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.cedenciasala.business.IBlocoCirurgicoCedenciaSalaFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class BloqueioSalaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 inicio();
	}
	

	private static final long serialVersionUID = 5878396916573469841L;
	
	@EJB
	private IBlocoCirurgicoCedenciaSalaFacade blocoCirurgicoCedenciaSalaFacade;	
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;	
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	private MbcBloqSalaCirurgica bloqueioSalaCirurgica;
		
	private Boolean ativoBloqueio;
		
	private LinhaReportVO equipe;	
	
	private Boolean ativo;
	
	private Short unidadeSalaCirurgica;
	
	private final String PAGE_BLOQUEAR_SALA_LIST = "bloqueioSalaList";
	
	public void inicio(){
        equipe = null;
		bloqueioSalaCirurgica = new MbcBloqSalaCirurgica();	
		if (unidadeSalaCirurgica!=null){
			bloqueioSalaCirurgica.setUnidadeSalaCirurgica(aghuFacade.obterAghUnidFuncionaisPeloId(unidadeSalaCirurgica));
		}
		setAtivoBloqueio(true);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidades(String objPesquisa) {	
		return this.returnSGWithCount(getAghuFacade().pesquisarUnidadesExecutorasCirurgias((String) objPesquisa),pesquisarUnidadesCount(objPesquisa));
	}

	public Long pesquisarUnidadesCount(String objPesquisa) {
		return getAghuFacade().pesquisarUnidadesExecutorasCirurgiasCount((String) objPesquisa);
	}
	
	public List<MbcSalaCirurgica> buscarSalasCirurgicas(String objPesquisa){
		String pesquisa = objPesquisa != null ? objPesquisa : null;
		return blocoCirurgicoCadastroApoioFacade.buscarSalasCirurgicas(pesquisa, bloqueioSalaCirurgica.getUnidadeSalaCirurgica().getSeq(), null);
	}
	
	public List<MbcTurnos> pesquisarTurnos() {	
		if(bloqueioSalaCirurgica.getUnidadeSalaCirurgica()== null){
			return blocoCirurgicoCedenciaSalaFacade.pesquisarTurnos(null);
		}else{
			return blocoCirurgicoCedenciaSalaFacade.pesquisarTurnosPorUnidade(null, 
				bloqueioSalaCirurgica.getUnidadeSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getUnidadeSalaCirurgica().getSeq());
		}
	}
	
	public List<LinhaReportVO> pesquisarEquipes(String pesquisa){
		return this.returnSGWithCount(blocoCirurgicoCedenciaSalaFacade.pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(pesquisa, 
				bloqueioSalaCirurgica.getUnidadeSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getUnidadeSalaCirurgica().getSeq(),
				bloqueioSalaCirurgica.getMbcSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getMbcSalaCirurgica().getId().getSeqp(),
				bloqueioSalaCirurgica.getDiaSemana() == null ? null : bloqueioSalaCirurgica.getDiaSemana(),
				bloqueioSalaCirurgica.getTurno() == null ? null : bloqueioSalaCirurgica.getTurno().getTurno(),
				DominioSituacao.A, false , DominioFuncaoProfissional.MPF, DominioFuncaoProfissional.MCO),pesquisarEquipesCount(pesquisa));
	}
	
	public Long pesquisarEquipesCount(String pesquisa){
		return blocoCirurgicoCedenciaSalaFacade.pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(pesquisa, 
				bloqueioSalaCirurgica.getUnidadeSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getUnidadeSalaCirurgica().getSeq(),
				bloqueioSalaCirurgica.getMbcSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getMbcSalaCirurgica().getId().getSeqp(),
				bloqueioSalaCirurgica.getDiaSemana() == null ? null : bloqueioSalaCirurgica.getDiaSemana(),
				bloqueioSalaCirurgica.getTurno() == null ? null : bloqueioSalaCirurgica.getTurno().getTurno(), 
				DominioSituacao.A,false, DominioFuncaoProfissional.MPF, DominioFuncaoProfissional.MCO);
	}		
	
	public String cancelar() {			
		setBloqueioSalaCirurgica(new MbcBloqSalaCirurgica());
		setEquipe(null);			
		return PAGE_BLOQUEAR_SALA_LIST;
	}	
		
	public void limparSuggestions() {					
		bloqueioSalaCirurgica.setMbcSalaCirurgica(null);	
		limparSuggestionEquipe();
		bloqueioSalaCirurgica.setTurno(null);
	}
	
	public void limparSuggestionEquipe() {			
		setEquipe(null);
	}	
		
	public String gravarBloqueioSala() {
		if (bloqueioSalaCirurgica.getDtInicio() != null && bloqueioSalaCirurgica.getDtFim() != null &&
				bloqueioSalaCirurgica.getDtInicio().after(bloqueioSalaCirurgica.getDtFim())){
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_BLOQUEIO_SALA_DATA_INICIAL_MAIOR_QUE_FINAL");
			return null;
		}		

		try {
			if (ativoBloqueio) {
				bloqueioSalaCirurgica.setIndSituacao(DominioSituacao.A);
			} else {
				bloqueioSalaCirurgica.setIndSituacao(DominioSituacao.I);
			}
			String mensagemSucesso = blocoCirurgicoCedenciaSalaFacade.gravarMbcBloqSalaCirurgica(bloqueioSalaCirurgica, equipe);
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso, bloqueioSalaCirurgica.getMbcSalaCirurgica().getId().getSeqp());								
			
			return PAGE_BLOQUEAR_SALA_LIST;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);	
			return null;
		}			
	}
	
	
	// Getters e Setters 
	
	public DominioDiaSemanaSigla[] getDominioDiaSemanaSigla() {
		return DominioDiaSemanaSigla.values();
	}

	public IBlocoCirurgicoCedenciaSalaFacade getBlocoCirurgicoCedenciaSalaFacade() {
		return blocoCirurgicoCedenciaSalaFacade;
	}

	public void setBlocoCirurgicoCedenciaSalaFacade(
			IBlocoCirurgicoCedenciaSalaFacade blocoCirurgicoCedenciaSalaFacade) {
		this.blocoCirurgicoCedenciaSalaFacade = blocoCirurgicoCedenciaSalaFacade;
	}

	public MbcBloqSalaCirurgica getBloqueioSalaCirurgica() {
		return bloqueioSalaCirurgica;
	}

	public void setBloqueioSalaCirurgica(MbcBloqSalaCirurgica bloqueioSalaCirurgica) {
		this.bloqueioSalaCirurgica = bloqueioSalaCirurgica;
	}

	public LinhaReportVO getEquipe() {
		return equipe;
	}

	public void setEquipe(LinhaReportVO equipe) {
		this.equipe = equipe;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public Boolean getAtivoBloqueio() {
		return ativoBloqueio;
	}

	public void setAtivoBloqueio(Boolean ativoBloqueio) {
		this.ativoBloqueio = ativoBloqueio;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Short getUnidadeSalaCirurgica() {
		return unidadeSalaCirurgica;
	}

	public void setUnidadeSalaCirurgica(Short unidadeSalaCirurgica) {
		this.unidadeSalaCirurgica = unidadeSalaCirurgica;
	}


					
}
