package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcEscalaProfUnidCirg;
import br.gov.mec.aghu.model.MbcEscalaProfUnidCirgId;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroEscalaProfissionaisPorSalaController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcEscalaProfUnidCirg> dataModel;

	private static final Log LOG = LogFactory.getLog(CadastroEscalaProfissionaisPorSalaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 5256302467213909521L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	//@Out(required = false)
	private DominioDiaSemana diaSemana = null;

	private DominioFuncaoProfissional funcaoProfissional;
	
	private MbcEscalaProfUnidCirg escalaProfUnidCirg;

	private MbcCaracteristicaSalaCirg caracteristicaSalaCirg;
	private MbcSalaCirurgica salaCirurgica;
	private MbcTurnos turnos;
	private MbcProfAtuaUnidCirgs profAtuaUnidCirgs;

	private AghUnidadesFuncionais unidadeFuncional;
	private RapServidores profissionalServ;
	
	private Boolean editarRegistro = false;
	
	private List<MbcProfAtuaUnidCirgs> listaFuncaoProfissional;
	
	private enum EscalaProfissionaisExceptionCode implements BusinessExceptionCode{
		MENSAGEM_CARACTERISTICA_ESCALA_PROFISSIONAL, 
		MENSAGEM_PROFISSIONAL_ESCALA_PROFISSIONAL,
		MENSAGEM_UNIDADE_FUNCIONAL_ESCALA_PROFISSIONAL;
	}
	
	public void inicio(){		
		
		if(unidadeFuncional == null){
			unidadeFuncional = new AghUnidadesFuncionais();
		}
	
		/* Carrega Suggestion "Unidade Cirurgica" com a unidade cadastrada no computador */
		this.setUnidadeFuncional(this.carregarUnidadeFuncionalInicial());
	}
	
	/* Obtem unidade cirurgica cadastrada para o computador */
	private AghUnidadesFuncionais carregarUnidadeFuncionalInicial() {
		String nomeMicrocomputador = null;
			
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			return blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e1) {
			this.LOG.error("Exceção capturada:", e1);
		}
		return null;
	}
	
	/* Suggestion "Unidade Cirurgica" */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(String objUnidade) {
		return this.returnSGWithCount(blocoCirurgicoCadastroFacade.buscarUnidadesFuncionaisCirurgia(objUnidade),
				pesquisarUnidadesFuncionaisCount(objUnidade));
	}
	
	public Long pesquisarUnidadesFuncionaisCount(String objUnidade) {
		return blocoCirurgicoCadastroFacade.contarUnidadesFuncionaisCirurgia(unidadeFuncional);
	}
	
	/* Suggestion "Sala" */
	public List<MbcSalaCirurgica> pesquisarSalasCirurgicas(String objSalaCirurgica){
		return this.returnSGWithCount(
				blocoCirurgicoCadastroFacade.pesquisarSalasCirurgicasPorUnfSeqSeqpOuNome(objSalaCirurgica, unidadeFuncional),
				blocoCirurgicoCadastroFacade.pesquisarSalasCirurgicasPorUnfSeqSeqpOuNomeCount(objSalaCirurgica, unidadeFuncional));
	}
	
	/* Suggestion "Turno" */
	public List<MbcTurnos> pesquisarTurnos(String objTurno){
		return this.returnSGWithCount(blocoCirurgicoCadastroFacade.pesquisarTiposTurno(objTurno), 
				blocoCirurgicoCadastroFacade.pesquisarTiposTurnoCount(objTurno));
	}
	
	/*Select Função*/
	public List<DominioFuncaoProfissional> listarDominioFuncaoProfissional(){
		return blocoCirurgicoCadastroFacade.listarDominioFuncaoPorMedico();
	}
	
	/* Suggestion "Profissional" */
	public List<RapServidores> pesquisarProfissionais(String objProfissional){
		return this.returnSGWithCount(registroColaboradorFacade.pesquisarServidoresAtivosPendentes(objProfissional),pesquisarProfissionaisCount(objProfissional));
	}

	public Long pesquisarProfissionaisCount(String objProfissional){
		return registroColaboradorFacade.pesquisarServidoresAtivosPendentesCount(objProfissional);
	}
	
	/* Suggestion "Sala / Dia Semana / Turno" */
	public List<MbcCaracteristicaSalaCirg> pesquisarSalaDiaSemanaTurno(String objSalaDiaSemanaTurno){
		return this.returnSGWithCount(blocoCirurgicoCadastroFacade.pesquisarSalaDiaSemanaTurno(objSalaDiaSemanaTurno, unidadeFuncional) , 
				pesquisarSalaDiaSemanaCount(objSalaDiaSemanaTurno));
				
	}
	
	public Long pesquisarSalaDiaSemanaCount(String objSalaDiaSemanaTurno){
		return blocoCirurgicoCadastroFacade.pesquisarSalaDiaSemanaTurnoCount(objSalaDiaSemanaTurno, unidadeFuncional);
	}
	
	
	/*Suggestion "Função / Profissional"*/
	public List<MbcProfAtuaUnidCirgs> pesquisarFuncaoProfissional(String objFuncaoProfissional) throws ApplicationBusinessException{
		
		listaFuncaoProfissional = blocoCirurgicoCadastroFacade.pesquisarFuncaoProfissionalEscala(objFuncaoProfissional, unidadeFuncional);
		
		return this.returnSGWithCount(listaFuncaoProfissional, pesquisarFuncaoProfissionalEscalaCount(objFuncaoProfissional)); 
		 
	}
	
	public Long pesquisarFuncaoProfissionalEscalaCount(String objFuncaoProfissional){
		
		Long countListaFuncionario = Long.valueOf(listaFuncaoProfissional.size());
		
		return countListaFuncionario;
	}
	
	/* Metodo Pesquisar */
	public void pesquisar(){
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return blocoCirurgicoCadastroFacade.pesquisarProfissionaisEscalaPorSalaCount(unidadeFuncional, salaCirurgica, diaSemana, 
																			 turnos, funcaoProfissional, profissionalServ);
	}

	@Override
	public List<MbcEscalaProfUnidCirg> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {		
		return blocoCirurgicoCadastroFacade.pesquisarProfissionaisEscalaPorSala(unidadeFuncional, salaCirurgica, diaSemana, 
																				turnos, funcaoProfissional, profissionalServ, 
																				firstResult, maxResult, orderProperty, asc);
	}
	
	/* Método Limpar */
	public void limparPesquisa(){
		this.setProfissionalServ(null);
		this.setFuncaoProfissional(null);
		this.setTurnos(null);
		this.setDiaSemana(null);
		this.setSalaCirurgica(null);
		this.setUnidadeFuncional(null);
		this.setCaracteristicaSalaCirg(null);
		this.setProfAtuaUnidCirgs(null);
		this.setEditarRegistro(false);
		this.dataModel.limparPesquisa();
	}
	
	/* Método Excluir */
	public void excluir() {

		String msgRetorno = this.blocoCirurgicoCadastroFacade.excluirEscalaProfissionaisPorSala(escalaProfUnidCirg);		
		this.apresentarMsgNegocio(Severity.INFO, msgRetorno);
		this.pesquisar();
	}
	
	/* Metodo Editar */
   public void editar(MbcEscalaProfUnidCirg edicaoEscala) throws ApplicationBusinessException{
		
		if(edicaoEscala != null){

			caracteristicaSalaCirg = new MbcCaracteristicaSalaCirg();			
			this.setCaracteristicaSalaCirg(edicaoEscala.getMbcCaracteristicaSalaCirg());
			
			profAtuaUnidCirgs = new MbcProfAtuaUnidCirgs();
			this.setProfAtuaUnidCirgs(edicaoEscala.getMbcProfAtuaUnidCirgs());
			
			escalaProfUnidCirg = edicaoEscala;
			
			this.setEditarRegistro(true);

		}
	}
	
	/* Método Validar Campos */
	public void validarCampos(){
		
	 try {	
			if(unidadeFuncional == null){
				throw new ApplicationBusinessException(EscalaProfissionaisExceptionCode.MENSAGEM_UNIDADE_FUNCIONAL_ESCALA_PROFISSIONAL);
			}
			
			if(caracteristicaSalaCirg == null){
				throw new ApplicationBusinessException(EscalaProfissionaisExceptionCode.MENSAGEM_CARACTERISTICA_ESCALA_PROFISSIONAL);
			}
			
			if(profAtuaUnidCirgs == null){
				throw new ApplicationBusinessException(EscalaProfissionaisExceptionCode.MENSAGEM_PROFISSIONAL_ESCALA_PROFISSIONAL);
			}
			
			this.gravar();
		
		} catch (ApplicationBusinessException e) {
			LOG.error("Exceção capturada em 'CadastroEscalaProfissionaisPorSalaController', parametros nulos 'caracteristicaSalaCirg' e/ou 'profAtuaUnidCirgs'.");
			apresentarExcecaoNegocio(e);			
		}
		
	}
	
	/* Método Gravar */
	public void gravar() {

		try{
			
			
			
			if(editarRegistro & escalaProfUnidCirg != null){
				this.blocoCirurgicoCadastroFacade.deletarMbcEscalaProfUnidCirg(escalaProfUnidCirg);
			}

			/* Seta ID da Escala  de Proissionais */
			MbcEscalaProfUnidCirgId escalaNovoId = new MbcEscalaProfUnidCirgId();
			escalaNovoId.setPucUnfSeq(unidadeFuncional.getSeq());
			escalaNovoId.setCasSeq(caracteristicaSalaCirg.getSeq());
			escalaNovoId.setPucIndFuncaoProf(profAtuaUnidCirgs.getId().getIndFuncaoProf());
			escalaNovoId.setPucSerMatricula(profAtuaUnidCirgs.getRapServidores().getId().getMatricula());
			escalaNovoId.setPucSerVinCodigo(profAtuaUnidCirgs.getRapServidores().getId().getVinCodigo());
			
			/* Seta ID dentro da Escala */
			MbcEscalaProfUnidCirg escalaNovo = new MbcEscalaProfUnidCirg();
			escalaNovo.setId(escalaNovoId);
			escalaNovo.setCriadoEm(new Date());
			
			escalaNovo.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			
			escalaNovo.setMbcProfAtuaUnidCirgs(profAtuaUnidCirgs);
			
			escalaNovo.setMbcCaracteristicaSalaCirg(caracteristicaSalaCirg);
			
			this.blocoCirurgicoCadastroFacade.inserirMbcEscalaProfUnidCirg(escalaNovo);			
			
		} catch(ApplicationBusinessException e){
			LOG.error("Exceção capturada em 'MbcEscalaProfUnidCirgRN', metodo 'insert'.");
			apresentarExcecaoNegocio(e);
			
		} finally {
			this.setEditarRegistro(false);
			this.setCaracteristicaSalaCirg(null);
			this.setProfAtuaUnidCirgs(null);
			this.pesquisar();
		}
		
	}
	
	/* Método Cancelar Edição */
	public void cancelarEditacao(){
		this.setEditarRegistro(false);
		this.setCaracteristicaSalaCirg(null);
		this.setProfAtuaUnidCirgs(null);
	}
	
	/* Getters and Setters */
	public void setEscalaProfUnidCirg(MbcEscalaProfUnidCirg escalaProfUnidCirg) {
		this.escalaProfUnidCirg = escalaProfUnidCirg;
	}

	public MbcEscalaProfUnidCirg getEscalaProfUnidCirg() {
		return escalaProfUnidCirg;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}

	public MbcSalaCirurgica getSalaCirurgica() {
		return salaCirurgica;
	}

	public void setTurnos(MbcTurnos turnos) {
		this.turnos = turnos;
	}

	public MbcTurnos getTurnos() {
		return turnos;
	}

	public void setFuncaoProfissional(DominioFuncaoProfissional funcaoProfissional) {
		this.funcaoProfissional = funcaoProfissional;
	}

	public DominioFuncaoProfissional getFuncaoProfissional() {
		return funcaoProfissional;
	}

	public void setProfissionalServ(RapServidores profissionalServ) {
		this.profissionalServ = profissionalServ;
	}

	public RapServidores getProfissionalServ() {
		return profissionalServ;
	}

	public void setCaracteristicaSalaCirg(MbcCaracteristicaSalaCirg caracteristicaSalaCirg) {
		this.caracteristicaSalaCirg = caracteristicaSalaCirg;
	}

	public MbcCaracteristicaSalaCirg getCaracteristicaSalaCirg() {
		return caracteristicaSalaCirg;
	}

	public DominioDiaSemana getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(DominioDiaSemana diaSemana) {
		this.diaSemana = diaSemana;
	}

	

	public void setProfAtuaUnidCirgs(MbcProfAtuaUnidCirgs profAtuaUnidCirgs) {
		this.profAtuaUnidCirgs = profAtuaUnidCirgs;
	}

	public MbcProfAtuaUnidCirgs getProfAtuaUnidCirgs() {
		return profAtuaUnidCirgs;
	}

	public void setEditarRegistro(Boolean editarRegistro) {
		this.editarRegistro = editarRegistro;
	}

	public Boolean getEditarRegistro() {
		return editarRegistro;
	} 


	public DynamicDataModel<MbcEscalaProfUnidCirg> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcEscalaProfUnidCirg> dataModel) {
	 this.dataModel = dataModel;
	}
}
