package br.gov.mec.aghu.blococirurgico.cedenciasala.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.cedenciasala.business.IBlocoCirurgicoCedenciaSalaFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class BloqueioSalaListController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 bloqueioSalaCirurgica = new MbcBloqSalaCirurgica();	
	}

	@Inject @Paginator
	private DynamicDataModel<MbcBloqSalaCirurgica> dataModel;

	private static final Log LOG = LogFactory.getLog(BloqueioSalaListController.class);

	private static final long serialVersionUID = 5878396916573469841L;
	
	@EJB
	private IBlocoCirurgicoCedenciaSalaFacade blocoCirurgicoCedenciaSalaFacade;	
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	private MbcBloqSalaCirurgica bloqueioSalaCirurgica;	
	
	private MbcBloqSalaCirurgica bloqueioSalaCirurgicaSelecionado;	
	
	private LinhaReportVO equipe;	
	
	private final String PAGE_BLOQUEAR_SALA = "bloqueioSala";
	
		
		
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
		return blocoCirurgicoCedenciaSalaFacade.pesquisarTurnos(null);
	}
	
	public List<LinhaReportVO> pesquisarEquipes(String pesquisa){
		return this.returnSGWithCount(blocoCirurgicoCedenciaSalaFacade.pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(pesquisa, 
				bloqueioSalaCirurgica.getUnidadeSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getUnidadeSalaCirurgica().getSeq(),
				bloqueioSalaCirurgica.getMbcSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getMbcSalaCirurgica().getId().getSeqp(),
				bloqueioSalaCirurgica.getDiaSemana() == null ? null : bloqueioSalaCirurgica.getDiaSemana(),
				bloqueioSalaCirurgica.getTurno() == null ? null : bloqueioSalaCirurgica.getTurno().getTurno(),
				DominioSituacao.A, false, DominioFuncaoProfissional.MPF, DominioFuncaoProfissional.MCO), pesquisarEquipesCount(pesquisa, false));
	}

	public Long pesquisarEquipesCount(String pesquisa){
		return pesquisarEquipesCount(pesquisa, true);
	}	

	public Long pesquisarEquipesCount(String pesquisa, boolean matriculaLong){
		return blocoCirurgicoCedenciaSalaFacade.pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(pesquisa, 
				bloqueioSalaCirurgica.getUnidadeSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getUnidadeSalaCirurgica().getSeq(),
				bloqueioSalaCirurgica.getMbcSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getMbcSalaCirurgica().getId().getSeqp(),
				bloqueioSalaCirurgica.getDiaSemana() == null ? null : bloqueioSalaCirurgica.getDiaSemana(),
				bloqueioSalaCirurgica.getTurno() == null ? null : bloqueioSalaCirurgica.getTurno().getTurno(),
				DominioSituacao.A, matriculaLong, DominioFuncaoProfissional.MPF, DominioFuncaoProfissional.MCO);
	}	

	public void pesquisar(){
		if (bloqueioSalaCirurgica.getDtInicio() != null && bloqueioSalaCirurgica.getDtFim() != null &&
				bloqueioSalaCirurgica.getDtInicio().after(bloqueioSalaCirurgica.getDtFim())){
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_BLOQUEIO_SALA_DATA_INICIAL_MAIOR_QUE_FINAL");			
		}else{
			this.dataModel.reiniciarPaginator();
		}
	}
	
	public String obterEspecialidadeSigla(AghEspecialidades esp){
		if(esp != null && esp.getSigla() != null){
			return esp.getSigla();
		}else{
			return "";
		}
	}
	
	
	@Override
	public List <MbcBloqSalaCirurgica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return blocoCirurgicoCedenciaSalaFacade.pesquisarBloqSalaCirurgica(firstResult, maxResult, orderProperty, asc, 
				bloqueioSalaCirurgica.getUnidadeSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getUnidadeSalaCirurgica().getSigla(),
				bloqueioSalaCirurgica.getMbcSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getMbcSalaCirurgica().getId().getSeqp(),
				bloqueioSalaCirurgica.getDtInicio(), bloqueioSalaCirurgica.getDtFim(), bloqueioSalaCirurgica.getDiaSemana(), 
				bloqueioSalaCirurgica.getTurno() == null ? null : bloqueioSalaCirurgica.getTurno().getTurno(), 
				equipe == null ? null : equipe.getNumero4(), equipe == null ? null : equipe.getNumero11().intValue(),
				equipe == null ? null : equipe.getSeqEsp());		
	}

	@Override
	public Long recuperarCount() {
		return blocoCirurgicoCedenciaSalaFacade.pesquisarBloqSalaCirurgicaCount(				
				bloqueioSalaCirurgica.getUnidadeSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getUnidadeSalaCirurgica().getSigla(), 
				bloqueioSalaCirurgica.getMbcSalaCirurgica() == null ? null : bloqueioSalaCirurgica.getMbcSalaCirurgica().getId().getSeqp(),
				bloqueioSalaCirurgica.getDtInicio(), bloqueioSalaCirurgica.getDtFim(), bloqueioSalaCirurgica.getDiaSemana(), 
				bloqueioSalaCirurgica.getTurno() == null ? null : bloqueioSalaCirurgica.getTurno().getTurno(), 
				equipe == null ? null : equipe.getNumero4(), equipe == null ? null : equipe.getNumero11().intValue(),
				equipe == null ? null : equipe.getSeqEsp());	
	}	
	
	public void limparPesquisa() {
        setBloqueioSalaCirurgica(new MbcBloqSalaCirurgica());
		setEquipe(null);		
		this.dataModel.limparPesquisa();
	}	
	
	public void selecionarBloqueioSala(MbcBloqSalaCirurgica bloqueioSalaCirurgica) {
		this.bloqueioSalaCirurgicaSelecionado = bloqueioSalaCirurgica;
	}
	
	public void alterarSituacaoBloqueioSala() {
		try{
			blocoCirurgicoCedenciaSalaFacade.atualizarMbcBloqSalaCirurgica(bloqueioSalaCirurgicaSelecionado);			
			if(DominioSituacao.I.equals(bloqueioSalaCirurgicaSelecionado.getIndSituacao())){			
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INATIVACAO_BOQUEIO_SALA", bloqueioSalaCirurgicaSelecionado.getMbcSalaCirurgica().getId().getSeqp());
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATIVACAO_BOQUEIO_SALA", bloqueioSalaCirurgicaSelecionado.getMbcSalaCirurgica().getId().getSeqp());
			}			
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}		
	}	
	
	public void limparSuggestions() {					
		bloqueioSalaCirurgica.setMbcSalaCirurgica(null);	
		setEquipe(null);
	}		
	
	
	public String obterSalaCirNome(MbcSalaCirurgica sala){
		if (sala != null){
			MbcSalaCirurgica salaOriginal = this.blocoCirurgicoCadastroApoioFacade.obterSalaCirurgicaBySalaCirurgicaId(sala.getId().getSeqp(), sala.getId().getUnfSeq());
			if (salaOriginal != null){
				return salaOriginal.getNome();
			}
		}		
		return "";
	}
	
	public String obterTurnoDescricao(MbcTurnos turno){
		
		if (turno != null){
			MbcTurnos turnoOriginal = this.blocoCirurgicoCadastroApoioFacade.obterMbcTurnodById(turno.getTurno());
			if(turnoOriginal != null){
				return turnoOriginal.getDescricao();
			}
		}
		
		return "";
	}
	
	public String obterPessoaFisicaNome(MbcProfAtuaUnidCirgs profAtuUni){
		
		if(profAtuUni != null){
			MbcProfAtuaUnidCirgs profAtuUniOriginal = this.blocoCirurgicoCadastroApoioFacade.obterMbcProfAtuaUnidCirgsPorId(profAtuUni.getId());
			
			if (profAtuUniOriginal != null && profAtuUniOriginal.getRapServidores().getId() != null){
				RapServidores rapServidor = this.registroColaboradorFacade.obterServidor(profAtuUniOriginal.getRapServidores().getId());
				if (rapServidor != null && rapServidor.getPessoaFisica().getCodigo() != null){
					RapPessoasFisicas pesFis;
					try {
						pesFis = this.registroColaboradorFacade.obterPessoaFisica(rapServidor.getPessoaFisica().getCodigo());
						if (pesFis != null){
						   return pesFis.getNome();
						}
					} catch (ApplicationBusinessException e) {
						LOG.error("PESSOA FISICA NAO ENCONTRADA");
					}
					
				}
			}
			
		}		
		return "";
	}
	
	public String obterUnidadeFuncionalSigla(Short seq){
		if (seq != null){
			AghUnidadesFuncionais unidadeFuncional = this.aghuFacade.obterUnidadeFuncional(seq);
			if (unidadeFuncional != null){
				return unidadeFuncional.getSigla();
			}
		}		
		return "";
	}	
	
	public String bloquearSala(){
		return PAGE_BLOQUEAR_SALA;
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

	public MbcBloqSalaCirurgica getBloqueioSalaCirurgicaSelecionado() {
		return bloqueioSalaCirurgicaSelecionado;
	}

	public void setBloqueioSalaCirurgicaSelecionado(
			MbcBloqSalaCirurgica bloqueioSalaCirurgicaSelecionado) {
		this.bloqueioSalaCirurgicaSelecionado = bloqueioSalaCirurgicaSelecionado;
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


	public DynamicDataModel<MbcBloqSalaCirurgica> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcBloqSalaCirurgica> dataModel) {
	 this.dataModel = dataModel;
	}
}
