package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemEvolucaoDAO;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotificacaoGmrDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioControleSumarioAltaPendente;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelProjetoPacientesDAO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmSumarioAlta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAnamnesesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmEvolucoesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmLaudoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaPacCpaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaServEquipeDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaServResponsavelDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.VMpmListaPacInternadosDAO;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusAltaObito;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusAnamneseEvolucao;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusCertificaoDigital;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusEvolucao;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusExamesNaoVistos;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusPacientePesquisa;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusPendenciaDocumento;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusPrescricao;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO.StatusSumarioAlta;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ListaPacientesInternadosON extends BaseBusiness {

	private static final String ARGUMENTO_INVALIDO = "Argumento inválido";

	private static final Log LOG = LogFactory.getLog(ListaPacientesInternadosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private MpmAltaSumarioDAO mpmAltaSumarioDAO;

	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;

	@EJB
	private ICascaFacade cascaFacade;

	@Inject
	private MpmListaServResponsavelDAO mpmListaServResponsavelDAO;

	@Inject
	private MpmListaServEquipeDAO mpmListaServEquipeDAO;

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

	@Inject
	private AelProjetoPacientesDAO aelProjetoPacientesDAO;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private MpmLaudoDAO mpmLaudoDAO;

	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Inject
	private MpmListaPacCpaDAO mpmListaPacCpaDAO;

	@Inject
	private MamTipoItemEvolucaoDAO mamTipoItemEvolucaoDAO;

	@Inject
	private MamItemEvolucoesDAO mamItemEvolucoesDAO;

	@Inject
	private VMpmListaPacInternadosDAO vMpmListaPacInternadosDAO;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@Inject
	private MciNotificacaoGmrDAO mciNotificacaoGmrDAO; 

	@Inject
	private MpmAnamnesesDAO mpmAnamnesesDAO;

	@Inject
	private MpmEvolucoesDAO mpmEvolucoesDAO; 


	private static final long serialVersionUID = 4177536088062626713L;

	public enum ListaPacientesInternadosONExceptionCode implements BusinessExceptionCode {
		ERRO_INTERNACAO_COM_PROCEDIMENTO_CIRURGICO_SUS, ERRO_PERMISSAO_SUMARIO_ALTA, ERRO_PERMISSAO_SUMARIO_OBITO, ERRO_SEXO_PACIENTE_NAO_INFORMADO, ERRO_PERMISSAO_DIAGNOSTICOS;
	}

	/**
	 * Retorna a lista de pacientes internados do profissional fornecido, sem as
	 * flags de status, habilita/desabilita botões, etc.<br />
	 * 
	 * @param servidor
	 * @return
	 */
	public List<PacienteListaProfissionalVO> pesquisarListaPacientesResumo(RapServidores servidor) {
		List<PacienteListaProfissionalVO> result = new ArrayList<PacienteListaProfissionalVO>();

		List<AghAtendimentos> atendimentosEquipe = this.pesquisarAtendimentosEquipe(servidor);
		for (AghAtendimentos atendimento : atendimentosEquipe) {

			PacienteListaProfissionalVO vo = new PacienteListaProfissionalVO(atendimento);

			vo.setLocal(this.getDescricaoLocalizacao(atendimento));
			// se necessário mais dados no VO, verificar método
			// pesquisarListaPacientes

			result.add(vo);
		}

		return result;
	}

	/**
	 * Retorna a lista de pacientes internados do profissional fornecido.
	 * 
	 * @param servidor
	 * @return
	 * @throws BaseException
	 */
	public List<PacienteListaProfissionalVO> pesquisarListaPacientes(RapServidores servidor) throws BaseException {
		if (servidor == null) {
			throw new IllegalArgumentException("Argumento inválido.");
		}
		List<PacienteListaProfissionalVO> result = new ArrayList<PacienteListaProfissionalVO>();
		
		if (isUsarBusca(AghuParametrosEnum.PME_LISTA_PACIENTES_USAR_BUSCA_OTIMIZADA)) {
			 List<PacienteListaProfissionalVO> listaOtimizada = vMpmListaPacInternadosDAO.buscaListaPacientesInternados(servidor);
			 
			 result.addAll(listaOtimizada);
    	} else {
    	    final String SIGLA_TIPO_ITEM_EVOLUCAO = "C";
    
    	    List<Integer> listaPacientesProjetoPesquisa = new ArrayList<Integer>();
    	    List<Integer> listaPacientesComPendencia = new ArrayList<Integer>();
    	    List<Short> listaUnidadesPrescricaoEletronica = new ArrayList<Short>();
    
    	    List<AghAtendimentos> listaPaciente = pesquisarAtendimentosEquipe(servidor);
    
    	    obterCodigosComPendenciasComProjetoPesquisa(listaPaciente, listaPacientesComPendencia, listaPacientesProjetoPesquisa,
    		    listaUnidadesPrescricaoEletronica);
    
    	    Boolean categoria = validarCategoriaProfissional();
    	    List<MamTipoItemEvolucao> itensEvolucao = getMamTipoItemEvolucaoDAO().pesquisarListaTipoItemEvoulucaoPelaSigla(
    		    SIGLA_TIPO_ITEM_EVOLUCAO);
    	    Boolean bloqueiaPacEmergencia = DominioSimNao.S.toString().equals(
    		    this.getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_BLOQUEIA_PAC_EMERG));
    
    	    for (AghAtendimentos atendimento : listaPaciente) {
	    		PacienteListaProfissionalVO vo = new PacienteListaProfissionalVO(atendimento);
	    
	    		vo.setPossuiPlanoAltas(verificarPossuiPlanoAlta(atendimento));
	    		vo.setLocal(this.getDescricaoLocalizacao(atendimento));
	    		vo.setStatusPrescricao(obterIconePrescricao(atendimento, listaUnidadesPrescricaoEletronica));
	    		vo.setStatusSumarioAlta(obterIconeSumarioAlta(atendimento));
	    		vo.setStatusExamesNaoVistos(obterIconeResultadoExames(atendimento.getSeq(), servidor));
	    		vo.setStatusPendenciaDocumento(obterIconePendenciaDocumento(atendimento));
	    		vo.setStatusPacientePesquisa(obterIconePacientePesquisa(atendimento, listaPacientesProjetoPesquisa));
	    		vo.setStatusEvolucao(obterIconeEvolucao(atendimento, categoria, itensEvolucao));
	    
	    		if (listaPacientesComPendencia.contains(atendimento.getPaciente().getCodigo())) {
	    		    vo.setStatusCertificacaoDigital(StatusCertificaoDigital.PENDENTE);
	    		} else {
	    		    vo.setStatusCertificacaoDigital(null);
	    		}
	    		// habilita/desabilita 'Alta/Obito/Antecipar Sumário' da lista
	    		vo.setDisableButtonAltaObito(this.disableFuncionalidadeLista(atendimento, bloqueiaPacEmergencia));
	    		// habilita/desabilita 'Prescrever' da lista
	    		vo.setDisableButtonPrescrever(this.disablePrescrever(atendimento, listaUnidadesPrescricaoEletronica));
	    
	    		if (this.sumarioAltaObito(atendimento)) {
	    		    vo.setLabelAlta(StatusAltaObito.SUMARIO_ALTA);
	    		    vo.setLabelObito(StatusAltaObito.SUMARIO_OBITO);
	    		} else {
	    		    vo.setLabelAlta(StatusAltaObito.ALTA);
	    		    vo.setLabelObito(StatusAltaObito.OBITO);
	    		}
	    		
				vo.setEnableButtonAnamneseEvolucao(this.habilitarBotaoAnamneseEvolucao(vo.getAtdSeq(), vo.isDisableButtonPrescrever(), vo.isDisableButtonAltaObito()));
				vo.setStatusAnamneseEvolucao(this.obterIconeAnamneseEvolucao(vo.getAtdSeq(), vo.isEnableButtonAnamneseEvolucao()));
	    		vo.setIndGmr(mciNotificacaoGmrDAO.verificarNotificacaoGmrPorCodigo(atendimento.getPaciente().getCodigo()));
	    		// adiciona na coleção
	    		result.add(vo);
    	    }
    	}
    	return result;
	}
	
	/*
	 * Verifica a existencia de parametro de sistema para buscar pela View.<br/>
	 * 
	 * Caso nao exista o parametro definido ou o valor seja diferente de 'S', entao<br/>
	 * Executa a busca usando a regras implementadas no codigo java. Fazendo varios acessos a banco. 
	 */
	private boolean isUsarBusca(AghuParametrosEnum enumParametro) {
		if (parametroFacade.verificarExisteAghParametro(enumParametro)) {
			AghParametros param = parametroFacade.getAghParametro(enumParametro);
			String strUsarView = "N";
			if (param != null) {
				strUsarView = param.getVlrTexto();
			}
			return ("S".equalsIgnoreCase(strUsarView));
		}
		return false;
	}

	private Boolean validarCategoriaProfissional() throws ApplicationBusinessException {
		// busca qual é a categoria profissional enfermagem
		final Integer catefProfEnf = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_CATEG_PROF_ENF);
		// busca qual é a categoria profissional médico
		final Integer catefProfMed = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_CATEG_PROF_MEDICO);
		// categoria outros profissionais
		final Integer cateOutros = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_CATEG_PROF_OUTROS);
		final List<CseCategoriaProfissional> categorias = getCascaFacade().pesquisarCategoriaProfissional(this.getServidorLogadoFacade().obterServidorLogado());
		if(!categorias.isEmpty()){
			CseCategoriaProfissional categoria = categorias.get(0);
			if(catefProfMed == categoria.getSeq() || cateOutros == categoria.getSeq()){
				return false;
			} else if (catefProfEnf == categoria.getSeq()){
				return true;
			}			
		}
		return null;
	}	

	public void obterCodigosComPendenciasComProjetoPesquisa(List<AghAtendimentos> listaPaciente, List<Integer> listaPacientesComPendencia, List<Integer> listaPacientesEmProjetoPesquisa, List<Short> listaUnidadesPrescricaoEletronica){
		List<Object[]> listaPacientesPendencias = null;
		List<Object[]> listaPacientesPesquisa = null;
		List<Integer> listaPacCodigo = new ArrayList<Integer>();
		HashSet<Short> HasUnidades =  new HashSet<Short>();
		

		for (AghAtendimentos atendimento: listaPaciente){
			listaPacCodigo.add(atendimento.getPaciente().getCodigo());
			HasUnidades.add(atendimento.getUnidadeFuncional().getSeq());
		}
		
		if (!listaPaciente.isEmpty()){	
			listaPacientesPendencias = this.getCertificacaoDigitalFacade().countVersaoPacientes(listaPacCodigo);
			for (Object[] vetorObject: listaPacientesPendencias){
				listaPacientesComPendencia.add((Integer)vetorObject[0]);
			}

			listaPacientesPesquisa = this.getAelProjetoPacientesDAO().verificaPacienteEmProjetoPesquisa(listaPacCodigo);
			for (Object[] vetorObject: listaPacientesPesquisa){
				listaPacientesEmProjetoPesquisa.add((Integer)vetorObject[0]);
			}
			
			for(Short unidade : new ArrayList<Short>(HasUnidades)) {
				if(aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidade, 
						ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA)) {
					listaUnidadesPrescricaoEletronica.add(unidade);
				}
			}

		}
	}	

	private StatusExamesNaoVistos obterIconeResultadoExames(
			Integer atdSeq, RapServidores servidor) {
		
		if(getAelItemSolicitacaoExameDAO().pesquisarExamesResultadoNaoVisualizado(atdSeq)){
			return StatusExamesNaoVistos.RESULTADOS_NAO_VISUALIZADOS;
		} else if (getAelItemSolicitacaoExameDAO().pesquisarExamesResultadoVisualizadoOutroMedico(atdSeq, servidor)){
			return StatusExamesNaoVistos.RESULTADOS_VISUALIZADOS_OUTRO_MEDICO;
		} else {
			return null;
		}
	}	
	
	
	/***
	 * Método realiza pesquisa sobre as entidades AghAtendimentos e
	 * MpmControlPrevAltas, para verifica se existe alguma previsão de alta
	 * marcada para as proximas 48 hrs
	 * 
	 * @param seq
	 * @return
	 */

	public boolean verificarPossuiPlanoAlta(AghAtendimentos atendimento) {
		if (atendimento == null) {
			return false;
		} else {
			if (DominioOrigemAtendimento.I.equals(atendimento.getOrigem())) {
				return verificarPossuiPlanoAltaAtendimento(atendimento.getSeq());
			} else if (DominioOrigemAtendimento.N.equals(atendimento.getOrigem())) {
				if (atendimento.getAtendimentoMae() == null) {
					return false;
				}

				if (DominioOrigemAtendimento.I.equals(atendimento.getAtendimentoMae().getOrigem())) {
					return verificarPossuiPlanoAltaAtendimento(atendimento.getAtendimentoMae().getSeq());
				} else if (DominioOrigemAtendimento.N.equals(atendimento.getOrigem())) {
					return false;
				}
			}
		}

		return false;
	}

	private boolean verificarPossuiPlanoAltaAtendimento(Integer seq) {

		Object[] prescricaoMedicaVO = mpmPrescricaoMedicaDAO.verificarPossuiPlanoAlta(seq);

		if (prescricaoMedicaVO != null && prescricaoMedicaVO[0] != null
				&& DominioSimNao.S.toString().equals(prescricaoMedicaVO[1])) {
			Integer diferencaDias = DateUtil.diffInDaysInteger((Date) prescricaoMedicaVO[0], new Date());
			if (diferencaDias >= 0 && diferencaDias <= 2) {
				return true;
			}
		}

		return false;
	}

	public void obterCodigosComPendenciasComProjetoPesquisa(List<AghAtendimentos> listaPaciente,
			List<Integer> listaPacientesComPendencia, List<Integer> listaPacientesEmProjetoPesquisa) {
		List<Object[]> listaPacientesPendencias = null;
		List<Object[]> listaPacientesPesquisa = null;
		List<Integer> listaPacCodigo = new ArrayList<Integer>();

		for (AghAtendimentos atendimento : listaPaciente) {
			listaPacCodigo.add(atendimento.getPaciente().getCodigo());
		}

		if (!listaPaciente.isEmpty()) {
			listaPacientesPendencias = this.getCertificacaoDigitalFacade().countVersaoPacientes(listaPacCodigo);
			for (Object[] vetorObject : listaPacientesPendencias) {
				listaPacientesComPendencia.add((Integer) vetorObject[0]);
			}

			listaPacientesPesquisa = this.getAelProjetoPacientesDAO().verificaPacienteEmProjetoPesquisa(listaPacCodigo);
			for (Object[] vetorObject : listaPacientesPesquisa) {
				listaPacientesEmProjetoPesquisa.add((Integer) vetorObject[0]);
			}

		}
	}

	/**
	 * Retorna os atendimentos do servidor fornecido.<br />
	 * Atendimentos na equipe, atendimentos pelo responsável da equipe e
	 * atendimentos em acompanhamento pelo servidor fornecido.
	 * 
	 * @param servidor
	 * @return
	 */
	private List<AghAtendimentos> pesquisarAtendimentosEquipe(RapServidores servidor) {
		// servidor =
		// this.getRegistroColaboradorFacade().buscaServidor(servidor.getId());

		// 3. pacientes em atendimento na equipe
		final List<RapServidores> pacAtdEqpResp = this.getMpmListaServEquipeDAO()
				.listaProfissionaisRespEquipe(servidor);
		// 3.1. pacientes em atendimento pelo responsável
		pacAtdEqpResp.addAll(this.getMpmListaServResponsavelDAO().listaProfissionaisResponsaveis(servidor));

		// 4. pacientes em acompanhamento pelo profissional
		boolean mostrarPacientesCpa = getMpmListaPacCpaDAO().mostrarPacientesCpas(servidor);

		List<AghAtendimentos> listaPaciente = getAghuFacade().listarPaciente(servidor, pacAtdEqpResp,
				mostrarPacientesCpa);
		return listaPaciente;
	}

	

	private StatusAnamneseEvolucao obterIconeAnamneseEvolucao(Integer atdSeq, boolean enableButtonAnamneseEvolucao) {
		if(enableButtonAnamneseEvolucao) {
			MpmAnamneses anamnese = this.getMpmAnamnesesDAO().obterAnamneseAtendimento(atdSeq);
			if(anamnese == null || (anamnese != null && anamnese.getPendente() == DominioIndPendenteAmbulatorio.R)) {
				return StatusAnamneseEvolucao.ANAMNESE_NAO_REALIZADA;
			}
			if(anamnese != null && anamnese.getPendente() == DominioIndPendenteAmbulatorio.P) {
				return StatusAnamneseEvolucao.ANAMNESE_PENDENTE;
			}
			if(anamnese != null && anamnese.getPendente() == DominioIndPendenteAmbulatorio.V) {
				//Data Atual Zerada
				Date dataReferencia = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
				if(!this.getMpmEvolucoesDAO().verificarEvolucaoRealizadaPorSituacao(anamnese.getSeq(), 
						DominioIndPendenteAmbulatorio.R, dataReferencia, false)) {
					return StatusAnamneseEvolucao.EVOLUCAO_DO_DIA_NAO_REALIZADA;
				}
				if(this.getMpmEvolucoesDAO().verificarEvolucaoRealizadaPorSituacao(anamnese.getSeq(), 
						DominioIndPendenteAmbulatorio.P, dataReferencia, true)) {
					return StatusAnamneseEvolucao.EVOLUCAO_DO_DIA_PENDENTE;
				}
				if(!this.getMpmEvolucoesDAO().verificarEvolucaoSeguinteRealizadaPorSituacao(anamnese.getSeq(), 
						DominioIndPendenteAmbulatorio.V, dataReferencia)) {
					return StatusAnamneseEvolucao.EVOLUCAO_VENCE_NO_DIA_SEGUINTE;
				}				
			}
		}
		return null;
	}

	private boolean habilitarBotaoAnamneseEvolucao(Integer atdSeq, boolean disableButtonPrescrever, boolean disableButtonAltaObito) {
		if (atdSeq != null) {
			AghAtendimentos atendimento = this.getAghuFacade().obterAtendimentoEmAndamento(atdSeq);
			if(atendimento != null && !disableButtonPrescrever && !disableButtonAltaObito){
				if(this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(atendimento.getUnidadeFuncional().getSeq(), 
						ConstanteAghCaractUnidFuncionais.ANAMNESE_EVOLUCAO_ELETRONICA)) {	
					return true;
				}
			}			
		}
		return false;
	}
	
	/**
	 * Regra para habilitar/desabilitar o botão 'Prescrever'
	 * 
	 * @param atendimento
	 * @return
	 */
	private boolean disablePrescrever(AghAtendimentos atendimento, List<Short> listaUnidadesPrescricaoEletronica) {

		if (atendimento == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		if (listaUnidadesPrescricaoEletronica.contains(atendimento.getUnidadeFuncional().getSeq())
				&& DominioPacAtendimento.S.equals(atendimento.getIndPacAtendimento())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Retorna a descrição para a localização atual do atendimento fornecido.
	 * 
	 * @param atendimento
	 * @return
	 */
	public String getDescricaoLocalizacao(AghAtendimentos atendimento) {
		if (atendimento == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}
		return this.getDescricaoLocalizacao(atendimento.getLeito(), atendimento.getQuarto(),
				atendimento.getUnidadeFuncional());
	}

	/**
	 * Retornar informação referente ao Sumário de Alta
	 * 
	 * @param atendimento
	 */
	public StatusSumarioAlta obterIconeSumarioAlta(AghAtendimentos atendimento) {
		if (atendimento == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		if (atendimento.getIndPacAtendimento() == DominioPacAtendimento.N) {
			if (DominioControleSumarioAltaPendente.E.equals(atendimento.getCtrlSumrAltaPendente())) {
				return StatusSumarioAlta.SUMARIO_ALTA_NAO_ENTREGUE_SAMIS;
			} else {
				return StatusSumarioAlta.SUMARIO_ALTA_NAO_REALIZADO;
			}
		}

		return null;
	}

	/**
	 * Retornar informação referente a Pendência de Documentos
	 * 
	 * @param atendimento
	 * @return
	 */
	public StatusPendenciaDocumento obterIconePendenciaDocumento(AghAtendimentos atendimento) {
		if (atendimento == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		if (this.getMpmLaudoDAO().existePendenciaLaudo(atendimento)) {
			return StatusPendenciaDocumento.PENDENCIA_LAUDO_UTI;
		}

		return null;

	}

	public StatusPacientePesquisa obterIconePacientePesquisa(AghAtendimentos atendimento,
			List<Integer> listaPacientesEmProjetoPesquisa) {
		if (atendimento == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		if (listaPacientesEmProjetoPesquisa.contains(atendimento.getPaciente().getCodigo())) {
			return StatusPacientePesquisa.PACIENTE_PESQUISA;
		}

		return null;

	}

	public StatusEvolucao obterIconeEvolucao(AghAtendimentos atendimento, Boolean categoria,
			List<MamTipoItemEvolucao> itens) throws ApplicationBusinessException {
		if (atendimento == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}
		boolean origemInternacao = atendimento.getOrigem() != null ? atendimento.getOrigem().equals(
				DominioOrigemAtendimento.I) : false;
		if (origemInternacao) {
			List<MamEvolucoes> mamEvolucoes = this.getMamEvolucoesDAO().pesquisarEvolucoesPorAtendimento(
					atendimento.getSeq());
			for (MamEvolucoes evolucoes : mamEvolucoes) {
				if (validarDataEvolucao(evolucoes)) {
					return validarCategoriaEnfMedOutros(evolucoes, categoria, itens);
				}
			}
		}
		return null;
	}

	/**
	 * Retornar informação refere a situação da prescrição do paciente
	 * 
	 * @param atendimento
	 */
	public StatusPrescricao obterIconePrescricao(AghAtendimentos atendimento, List<Short> listaUnidadesPrescricaoEletronica) {
		if (atendimento == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		// Não buscar o ícone quando o botão 'Prescrever' estiver desabilitado.
		if (this.disablePrescrever(atendimento,listaUnidadesPrescricaoEletronica)) {
			return null;
		}

		MpmPrescricaoMedicaDAO mpmDao = this.getMpmPrescricaoMedicaDAO();

		if (!mpmDao.existePrescricaoEmDia(atendimento)) {
			return StatusPrescricao.PRESCRICAO_NAO_REALIZADA;
		} else {
			if (!mpmDao.existePrescricaoFutura(atendimento)) {
				return StatusPrescricao.PRESCRICAO_VENCE_NO_DIA;
			} else {
				return StatusPrescricao.PRESCRICAO_VENCE_NO_DIA_SEGUINTE;
			}
		}
	}

	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return certificacaoDigitalFacade;
	}

	/**
	 * Retorna descrição para a localização.<br>
	 * Retorna uma descrição de acordo com os argumentos fornecidos.
	 * 
	 * @param leito
	 * @param quarto
	 * @param unidade
	 * @return
	 */
	public String getDescricaoLocalizacao(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidade) {
		if (leito == null && quarto == null && unidade == null) {
			throw new IllegalArgumentException("Pelo menos um dos argumentos deve ser fornecido.");
		}

		if (leito != null) {
			return String.format("L:%s", leito.getLeitoID());
		}
		if (quarto != null) {
			return String.format("Q:%s", quarto.getDescricao());
		}

		String string = String.format("U:%s %s - %s", unidade.getAndar(), unidade.getIndAla(), unidade.getDescricao());

		return StringUtils.substring(string, 0, 8);
	}

	/**
	 * Regra que habilita/desabilita os botões 'Alta', 'Óbito' e 'Antecipar
	 * Sumário'
	 * 
	 * @param atendimento
	 * @return
	 * @throws BaseException
	 */
	public boolean disableFuncionalidadeLista(AghAtendimentos atendimento, Boolean bloqueiaPacEmergencia)
			throws BaseException {

		boolean disableAltaObito = false;

		if (atendimento == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		if (existeSumarioAltaComAltaMedica(atendimento) || existeAltaSumarioConcluido(atendimento)) {
			disableAltaObito = true;
		}

		if (pacienteUnidadeEmergencia(atendimento, bloqueiaPacEmergencia)) {
			disableAltaObito = true;
		}

		return disableAltaObito;
	}

	/**
	 * Regra para determinar se o LABEL será 'Alta/Óbito' ou 'Sumário
	 * Alta/Óbito'
	 * 
	 * @param atendimento
	 * @return
	 * @throws BaseException
	 */
	public boolean sumarioAltaObito(AghAtendimentos atendimento) throws BaseException {

		boolean sumarioAltaObito = false;

		if (atendimento == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		if (atendimento.getIndPacAtendimento() == DominioPacAtendimento.N
				&& !DominioControleSumarioAltaPendente.E.equals(atendimento.getCtrlSumrAltaPendente())) {
			sumarioAltaObito = true;
		}

		return sumarioAltaObito;
	}

	/**
	 * Retorna true se o motivo de alta for óbito.
	 * 
	 * @param atendimento
	 * @return retorna false se não for óbito ou não encontrou sumário de alta
	 *         ativo para o atendimento.
	 */
	public boolean isMotivoAltaObito(Integer atdSeq) throws ApplicationBusinessException {

		if (atdSeq == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		AghAtendimentos atendimento = getAghuFacade().obterAtendimentoPeloSeq(atdSeq);

		if (atendimento == null) {
			return false;
		}

		MpmAltaSumario sumario = this.getMpmAltaSumarioDAO().obterAltaSumarios(atendimento.getSeq());

		if (sumario == null || sumario.getAltaMotivos() == null) {
			return false;
		}

		return sumario.getAltaMotivos().getMotivoAltaMedicas().getIndObito();
	}

	/**
	 * Verificar se o atendimento do paciente está sendo realizado em uma
	 * unidade de emergência
	 * 
	 * @param atendimento
	 * @return
	 * @throws BaseException
	 */
	protected boolean pacienteUnidadeEmergencia(AghAtendimentos atendimento, Boolean bloqueiaPacEmergencia)
			throws ApplicationBusinessException {

		if (atendimento == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		if (!bloqueiaPacEmergencia) {
			return false;
		}

		if (aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(),
				ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO)) {
			return true;
		}

		return false;
	}

	/**
	 * Verificar se existe sumário de alta cujo o motivo da alta médica tenha
	 * sido informado
	 * 
	 * @param atendimento
	 * @return
	 */
	protected boolean existeSumarioAltaComAltaMedica(AghAtendimentos atendimento) {

		List<MpmSumarioAlta> list = new ArrayList<MpmSumarioAlta>(atendimento.getSumariosAlta());

		for (MpmSumarioAlta sumarioAltas : list) {
			if (sumarioAltas.getMotivoAltaMedica() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * Verificar se existe alta de sumários que já esteja concluído
	 * 
	 * @param atendimento
	 * @return
	 */
	protected boolean existeAltaSumarioConcluido(AghAtendimentos atendimento) {
		return getMpmAltaSumarioDAO().verificarAltaSumariosConcluido(atendimento.getSeq());
	}

	/**
	 * ORADB: Procedure MPMC_VER_PERM_ALTA
	 * 
	 * @param atendimento
	 * @throws BaseException
	 */
	private void verificarTempoPermanecia(Integer atdSeq) throws ApplicationBusinessException {

		if (atdSeq == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		AghAtendimentos atendimento = getAghuFacade().obterAtendimentoPeloSeq(atdSeq);

		if (atendimento == null) {
			return;
		}

		Date dataAtual = DateUtil.obterDataComHoraInical(null);

		if (atendimento.getDthrInicio().equals(dataAtual)
				&& atendimento.getIndPacAtendimento() == DominioPacAtendimento.S
				&& this.verificarConvenioSus(atendimento)
				&& getBlocoCirurgicoFacade().procedimentoCirurgicoExigeInternacao(atendimento)) {

			throw new ApplicationBusinessException(
					ListaPacientesInternadosONExceptionCode.ERRO_INTERNACAO_COM_PROCEDIMENTO_CIRURGICO_SUS);
		}

	}

	/**
	 * Realizar consistências antes da chamada do sumário de alta
	 * 
	 * @param atdSeq
	 * @throws ApplicationBusinessException
	 */
	public void realizarConsistenciasSumarioAlta(Integer atdSeq) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (!getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "pesquisarListaPacientesInternados",
				"acessarSumarioAlta")) {
			throw new ApplicationBusinessException(ListaPacientesInternadosONExceptionCode.ERRO_PERMISSAO_SUMARIO_ALTA);
		}

		if (atdSeq != null) {
			this.verificarTempoPermanecia(atdSeq);
		}

	}

	/**
	 * Realizar consistências antes da chamada do sumário de óbito
	 * 
	 * @param atdSeq
	 * @throws ApplicationBusinessException
	 */
	public void realizarConsistenciasSumarioObito(Integer atdSeq) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (!getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "pesquisarListaPacientesInternados",
				"acessarSumarioObito")) {
			throw new ApplicationBusinessException(ListaPacientesInternadosONExceptionCode.ERRO_PERMISSAO_SUMARIO_OBITO);
		}

		if (atdSeq != null) {
			this.verificarSexoCadastrado(atdSeq);
		}

	}

	/**
	 * Verificar se o paciente possui sexo cadastrado no sistema
	 * 
	 * @param atdSeq
	 * @throws ApplicationBusinessException
	 */
	private void verificarSexoCadastrado(Integer atdSeq) throws ApplicationBusinessException {

		if (atdSeq == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		AghAtendimentos atendimento = getAghuFacade().obterAtendimentoPeloSeq(atdSeq);

		if (atendimento != null && atendimento.getPaciente() != null && atendimento.getPaciente().getSexo() == null) {

			throw new ApplicationBusinessException(
					ListaPacientesInternadosONExceptionCode.ERRO_SEXO_PACIENTE_NAO_INFORMADO);
		}

	}

	/**
	 * Icone da evolucao pode ser visto apenas quando ela feita no dia
	 * 
	 * @param mamEvolucoes
	 */
	private boolean validarDataEvolucao(MamEvolucoes mamEvolucoes) {
		Calendar dataHoje = Calendar.getInstance();
		Calendar dataValida = Calendar.getInstance();
		dataHoje.setTime(new Date());
		boolean hoje = false;
		boolean dataHoraMovimentacao = false;
		dataValida.setTime(mamEvolucoes.getDthrValida());
		hoje = dataHoje.get(Calendar.YEAR) == dataValida.get(Calendar.YEAR)
				&& dataHoje.get(Calendar.DAY_OF_YEAR) == dataValida.get(Calendar.DAY_OF_YEAR);
		dataHoraMovimentacao = mamEvolucoes.getDthrValidaMvto() == null ? true : false;
		return hoje && dataHoraMovimentacao;
	}

	/**
	 * Apenas os profissionais dos parametros abaixo definidos podem ver o icone
	 * evolucao
	 * 
	 * @param mamEvolucoes
	 * @throws ApplicationBusinessException
	 */
	private StatusEvolucao validarCategoriaEnfMedOutros(MamEvolucoes mamEvolucoes, Boolean categoria,
			List<MamTipoItemEvolucao> itens) throws ApplicationBusinessException {
		if (Boolean.FALSE.equals(categoria)) {
			return StatusEvolucao.EVOLUCAO;
		} else if (Boolean.TRUE.equals(categoria)) {
			for (MamTipoItemEvolucao mamTipoItemEvolucao : itens) {
				if (this.getMamItemEvolucoesDAO().pesquisarExisteItemEvolucaoPorEvolucaoTipoItem(mamEvolucoes.getSeq(),
						mamTipoItemEvolucao.getSeq())) {
					return StatusEvolucao.EVOLUCAO;
				}
			}
		}
		return null;
	}

	/**
	 * Realizar consistências antes da chamada de Diagnósticos
	 * 
	 * @param atdSeq
	 * @throws ApplicationBusinessException
	 */
	public void realizarConsistenciasDiagnosticos() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (!getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "pesquisarListaPacientesInternados",
				"acessarDiagnosticos")) {
			throw new ApplicationBusinessException(ListaPacientesInternadosONExceptionCode.ERRO_PERMISSAO_DIAGNOSTICOS);
		}

	}

	/**
	 * Verificar se o convênio do atendimento é SUS
	 * 
	 * @param atendimento
	 * @return
	 * @throws BaseException
	 */
	public boolean verificarConvenioSus(AghAtendimentos atendimento) throws ApplicationBusinessException {

		boolean isConvenioSus = false;

		if (atendimento == null || atendimento.getInternacao() == null
				|| atendimento.getInternacao().getConvenioSaude() == null
				|| atendimento.getInternacao().getConvenioSaude().getPagador() == null
				|| atendimento.getInternacao().getConvenioSaude().getPagador().getSeq() == null) {
			return isConvenioSus;
		}

		AghParametros convenioSus = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);

		if (atendimento.getInternacao().getConvenioSaude().getPagador().getSeq() == convenioSus.getVlrNumerico()
				.shortValue()) {
			isConvenioSus = true;
		}

		return isConvenioSus;
	}

	public Integer recuperarAtendimentoPaciente(Integer altanAtdSeq) throws ApplicationBusinessException {
		return this.getAghuFacade().recuperarAtendimentoPaciente(altanAtdSeq);
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	protected MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected MpmLaudoDAO getMpmLaudoDAO() {
		return mpmLaudoDAO;
	}

	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	protected ICascaFacade getICascaFacade() {
		return this.cascaFacade;
	}

	protected MpmListaServEquipeDAO getMpmListaServEquipeDAO() {
		return mpmListaServEquipeDAO;
	}

	protected MpmListaServResponsavelDAO getMpmListaServResponsavelDAO() {
		return mpmListaServResponsavelDAO;
	}

	protected MpmListaPacCpaDAO getMpmListaPacCpaDAO() {
		return mpmListaPacCpaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected AelProjetoPacientesDAO getAelProjetoPacientesDAO() {
		return aelProjetoPacientesDAO;
	}

	protected MamEvolucoesDAO getMamEvolucoesDAO() {
		return mamEvolucoesDAO;
	}

	protected MamTipoItemEvolucaoDAO getMamTipoItemEvolucaoDAO() {
		return mamTipoItemEvolucaoDAO;
	}

	protected MamItemEvolucoesDAO getMamItemEvolucoesDAO() {
		return mamItemEvolucoesDAO;
	}

	public MpmAnamnesesDAO getMpmAnamnesesDAO() {
		return mpmAnamnesesDAO;
	}

	public void setMpmAnamnesesDAO(MpmAnamnesesDAO mpmAnamnesesDAO) {
		this.mpmAnamnesesDAO = mpmAnamnesesDAO;
	}

	public void setMamEvolucoesDAO(MamEvolucoesDAO mamEvolucoesDAO) {
		this.mamEvolucoesDAO = mamEvolucoesDAO;
	}

	public MpmEvolucoesDAO getMpmEvolucoesDAO() {
		return mpmEvolucoesDAO;
	}

	public void setMpmEvolucoesDAO(MpmEvolucoesDAO mpmEvolucoesDAO) {
		this.mpmEvolucoesDAO = mpmEvolucoesDAO;
	}

}
