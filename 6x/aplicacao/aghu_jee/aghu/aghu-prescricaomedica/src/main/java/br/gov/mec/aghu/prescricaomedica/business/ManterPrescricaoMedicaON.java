package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghDocumento;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.MpmAltaRecomendacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoCuidadoId;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNptId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoriaId;
import br.gov.mec.aghu.model.MpmSumarioAlta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaRecomendacaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmComposicaoPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModoUsoPrescProcedDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSumarioAltaDAO;
import br.gov.mec.aghu.prescricaomedica.util.PrescricaoMedicaTypes;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * 
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class ManterPrescricaoMedicaON extends BaseBusiness {

	@EJB
	private PrescreverProcedimentosEspeciaisON prescreverProcedimentosEspeciaisON;
	
	@EJB
	private ConsultoriaON consultoriaON;
	
	@EJB
	private ManterPrescricaoMedicaRN manterPrescricaoMedicaRN;
	
	@EJB
	private PrescricaoNptRN prescricaoNptRN;
	
	@EJB
	private DocumentoON documentoON;
	
	private static final Log LOG = LogFactory.getLog(ManterPrescricaoMedicaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;
	
	@Inject
	private MpmAltaSumarioDAO mpmAltaSumarioDAO;
	
	@Inject
	private MpmItemPrescricaoDietaDAO mpmItemPrescricaoDietaDAO;
	
	@Inject
	private MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO;
	
	@Inject
	private MpmModoUsoPrescProcedDAO mpmModoUsoPrescProcedDAO;
	
	@Inject
	private MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@Inject
	private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;
	
	@Inject
	private MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO;
	
	@Inject
	private MpmSumarioAltaDAO mpmSumarioAltaDAO;
	
	@Inject
	private MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO;
	
	@Inject
	private MpmComposicaoPrescricaoNptDAO mpmComposicaoPrescricaoNptDAO;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;
	
	@Inject
	private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;

	@Inject
	private MpmAltaRecomendacaoDAO mpmAltaRecomendacaoDAO;

	@EJB
	private ManterAltaRecomendacaoRN manterAltaRecomendacaoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5477946191934254343L;
	private static final String CARACTER_ESPACO = " ";

	public enum ManterPrescricaoMedicaExceptionCode implements
			BusinessExceptionCode {

		ERRO_MONTAR_LISTA_NUTRICAO_PARENTERAL
		, MSG_ADVERTENCIA_NENHUMA_ITEM_SELECIONADO_PARA_EXCLUSAO, MPM_02319, MPM_02320, MPM_02321,
		MENSAGEM_NAO_EXISTEM_ITENS_CONTRACHEQUE, OPTIMISTIC_LOCK;

		public void throwException(Throwable cause, Object... params)
				throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}

	private static final String SEPARADOR_LINHA = "<br/>";

	/**
	 * @author mtocchetto
	 * @param filter
	 *            Filtros para buscar o VO da prescreição médica.
	 * @return PrescricaoMedicaVO VO utilizado em "Manter Prescrição Médica".
	 * @throws ApplicationBusinessException
	 */
	public PrescricaoMedicaVO buscarDadosCabecalhoPrescricaoMedicaVO(
			MpmPrescricaoMedicaId prescricaoMedicaId)
			throws ApplicationBusinessException {
		if (prescricaoMedicaId == null) {
			throw new IllegalArgumentException(
					"buscarDadosCabecalhoPrescricaoMedicaVO: parametros de filtro invalido");
		}

		MpmPrescricaoMedica mpmPrescricaoMedica = this.getPrescricaoMedicaDAO()
				.obterPorChavePrimaria(prescricaoMedicaId, MpmPrescricaoMedica.Fields.ATENDIMENTO);
		
		// Correção #44903, evitar o lazy
		if(!mpmPrescricaoMedica.getAtendimento().getCirurgias().isEmpty()){
			mpmPrescricaoMedica.getAtendimento().getCirurgias().iterator().next().getConvenioSaude();
		}
		
		if (mpmPrescricaoMedica.getDthrMovimento() == null){
			mpmPrescricaoMedica.setDthrMovimento(new Date());			
		}
//		if (mpmPrescricaoMedica.getSituacao() == DominioSituacaoPrescricao.L){
//			mpmPrescricaoMedica.setSituacao(DominioSituacaoPrescricao.U);
//		}
		
		if(mpmPrescricaoMedica.getAtendimento() != null){
			if(mpmPrescricaoMedica.getAtendimento().getUnidadeFuncional() != null){
				if(mpmPrescricaoMedica.getAtendimento().getUnidadeFuncional().getCaracteristicas() != null){
					LOG.debug("carregando caracateristicas "+mpmPrescricaoMedica.getAtendimento().getUnidadeFuncional().getCaracteristicas().size());
				}
			}
			
			if(mpmPrescricaoMedica.getAtendimento().getInternacao() != null){
				if(mpmPrescricaoMedica.getAtendimento().getInternacao().getItemProcedimentoHospitalar() != null){
					LOG.debug("carregando quantidade dias faturamento "+mpmPrescricaoMedica.getAtendimento().getInternacao().getItemProcedimentoHospitalar().getQuantDiasFaturamento());
				}
			}
			
			
			if(mpmPrescricaoMedica.getAtendimento().getEspecialidade() != null){
				if(mpmPrescricaoMedica.getAtendimento().getEspecialidade().getClinica() != null){
					LOG.debug("carregando descrição da clínica " + mpmPrescricaoMedica.getAtendimento().getEspecialidade().getClinica().getDescricao());
				}
			}
			
		}
		
		getPrescricaoMedicaDAO().initialize(mpmPrescricaoMedica.getServidorValida());
		
		PrescricaoMedicaVO prescricaoMedicaVO = new PrescricaoMedicaVO();
		popularDadosCabecalhoPrescricaoMedicaVO(prescricaoMedicaVO,mpmPrescricaoMedica);

		return prescricaoMedicaVO;
	}

	protected MpmPrescricaoMedicaDAO getPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

	protected MpmSolicitacaoConsultoriaDAO getSolicitacaoConsultoriasDAO() {
		return mpmSolicitacaoConsultoriaDAO;
	}

	private MpmPrescricaoNptDAO getPrescricaoNptsDAO() {
		return mpmPrescricaoNptDAO;
	}

	private MpmComposicaoPrescricaoNptDAO getComposicaoPrescricaoNptsDAO() {
		return mpmComposicaoPrescricaoNptDAO;
	}

	private MpmItemPrescricaoNptDAO getItemPrescricaoNptDAO() {
		return mpmItemPrescricaoNptDAO;
	}

	/**
	 * @author mtocchetto
	 * @param prescricaoMedicaVO
	 *            VO que armazena os dados do cabeçalho.
	 * @param mpmPrescricaoMedica
	 *            Contém as informações necessárias para popular os dados do
	 *            cabeçalho no VO.
	 * @throws ApplicationBusinessException
	 */
	protected void popularDadosCabecalhoPrescricaoMedicaVO(
			PrescricaoMedicaVO prescricaoMedicaVO,
			MpmPrescricaoMedica mpmPrescricaoMedica)
			throws ApplicationBusinessException {
		ManterPrescricaoMedicaRN manterPrescricaoMedicaRN = getManterPrescricaoMedicaRN();

		AghAtendimentos aghAtendimentos = mpmPrescricaoMedica.getAtendimento();
		aghAtendimentos.getCidAtendimentos();
		AipPacientes aipPacientes = aghAtendimentos.getPaciente();

		// Dados de MpmPrescricaoMedica
		Date dthrInicio = mpmPrescricaoMedica.getDthrInicio();
		Date dthrFim = mpmPrescricaoMedica.getDthrFim();

		// Dados de AghAtendimentos
		Boolean indPacPediatrico = aghAtendimentos.getIndPacPediatrico();
		String local = manterPrescricaoMedicaRN
				.buscarResumoLocalPaciente(aghAtendimentos);

		// Dados de AipPacientes
		Integer prontuario = aipPacientes.getProntuario();
		String nome =alteraCaixaAltaNome(aipPacientes.getNome());
		
		String nomeSocial= null;
		if(StringUtils.isNotBlank((aipPacientes.getNomeSocial()))){
			nomeSocial = alteraCaixaAltaNome(aipPacientes.getNomeSocial());
		}
		// Dados de AinInternacao
		boolean hasInternacao = true;
		Date dtPrevAlta = null;
		if (aghAtendimentos.getInternacao() != null) {
			dtPrevAlta = aghAtendimentos.getInternacao().getDtPrevAlta();
		} else {
			hasInternacao = false;
		}
		
		// Popula campos de PrescricaoMedicaVO
		prescricaoMedicaVO.setPrescricaoMedica(mpmPrescricaoMedica);
		prescricaoMedicaVO.setDthrInicio(dthrInicio);
		prescricaoMedicaVO.setDthrFim(dthrFim);
		prescricaoMedicaVO.setIndPacPediatrico(indPacPediatrico);
		prescricaoMedicaVO.setLocal(local);
		prescricaoMedicaVO.setProntuario(prontuario);
		prescricaoMedicaVO.setNome(nome);
		prescricaoMedicaVO.setNomeSocial(nomeSocial);
		prescricaoMedicaVO.setDtPrevAlta(dtPrevAlta);
		prescricaoMedicaVO.setHasInternacao(hasInternacao);
				
	}

	/**
	 * rcorvalao 22/09/2010
	 * 
	 * @ORADB PROCEDURE MPMP_POPULA_SINTAXE
	 * @param umFilter
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ItemPrescricaoMedicaVO> buscarItensPrescricaoMedica(
			MpmPrescricaoMedicaId prescricaoMedicaId, Boolean listarTodas)
			throws ApplicationBusinessException {
		return buscarItensPrescricaoMedica(prescricaoMedicaId, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, listarTodas);
	}
	
	/**
	 * Busca os itens de Prescricao Medica para apresentacao na Lista de Itens<br>
	 * Recomendados no Plano Pos Alta no Sumario  de Alta.<br>
	 * Apenas Dieta, Cuidados e Medicamentos.<br>
	 * 
	 * 
	 * @param umFilter
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ItemPrescricaoMedicaVO> buscarItensPrescricaoMedicaPlanoPosAlta(MpmPrescricaoMedicaId prescricaoMedicaId) throws ApplicationBusinessException {
		return buscarItensPrescricaoMedica(prescricaoMedicaId, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE,
				Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
	}

	public List<ItemPrescricaoMedicaVO> buscarItensPrescricaoMedica(MpmPrescricaoMedicaId prescricaoMedicaId,
			Boolean dieta, Boolean cuidadoMedico, Boolean medicamento, 
			Boolean solucao, Boolean consultoria, Boolean hemoterapia, 
			Boolean nutricaoParental, Boolean procedimento, Boolean listarTodas
			) throws ApplicationBusinessException {
		validarPrescricaoMedica(prescricaoMedicaId);
		
		PrescricaoMedicaVO prescricaoMedicaVO = new PrescricaoMedicaVO();
		
		if(dieta){
			this.populaDieta(prescricaoMedicaVO, prescricaoMedicaId, listarTodas);
		}
		if(cuidadoMedico){
			this.populaCuidadoMedico(prescricaoMedicaVO, prescricaoMedicaId, listarTodas);
		}
		if(medicamento){
			this.populaMedicamento(prescricaoMedicaVO, prescricaoMedicaId, listarTodas);
		}
		if(solucao){
			this.populaSolucao(prescricaoMedicaVO, prescricaoMedicaId, listarTodas);
		}
		if(consultoria){
			this.populaConsultoria(prescricaoMedicaVO, prescricaoMedicaId, listarTodas);
		}
		if(hemoterapia){
			this.populaHemoterapia(prescricaoMedicaVO, prescricaoMedicaId, listarTodas);
		}
		if(nutricaoParental){
			this.populaNutricaoParental(prescricaoMedicaVO, prescricaoMedicaId, listarTodas); // NPT
		}
		if(procedimento){
			this.populaProcedimento(prescricaoMedicaVO, prescricaoMedicaId, listarTodas);
		}

		return prescricaoMedicaVO.getItens();
	}

	private void validarPrescricaoMedica(
			MpmPrescricaoMedicaId prescricaoMedicaId) {
		if (prescricaoMedicaId == null || prescricaoMedicaId.getAtdSeq() == null || prescricaoMedicaId.getSeq() == null) {
			throw new IllegalArgumentException(
					"buscarPrescricaoMedica: parametros de filtro invalido");
		}
	}

	/**
	 * rcorvalao 22/09/2010 
	 * cvirgens 30/09/2010
	 * 
	 * @param prescricaoMedicaVO
	 * @param prescricaoMedicaId
	 */
	private void populaProcedimento(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoMedicaId prescricaoMedicaId, Boolean listarTodas) {
		MpmPrescricaoMedica prescricaoMedica = this.getPrescricaoMedicaDAO().obterPorChavePrimaria(prescricaoMedicaId);

		if (prescricaoMedica != null) {
			List<MpmPrescricaoProcedimento> list = this.getMpmPrescricaoProcedimentoDAO().buscaPrescricaoProcedimentos(prescricaoMedicaId, prescricaoMedica.getDthrFim(), listarTodas);
			for (MpmPrescricaoProcedimento mpmPrescricaoProcedimento : list) {
				this.getMpmPrescricaoProcedimentoDAO().refresh(mpmPrescricaoProcedimento);
				for (MpmModoUsoPrescProced mpmModoUsoPrescProced : mpmPrescricaoProcedimento.getModoUsoPrescricaoProcedimentos()) {
					this.getMpmModoUsoPrescProcedsDAO().refresh(mpmModoUsoPrescProced);
				}
			}
			
			List<MpmPrescricaoProcedimento> listOriginal = (List<MpmPrescricaoProcedimento>)(new ArrayList<MpmPrescricaoProcedimento>(list)).clone();
			if(listarTodas) {
				CollectionUtils.filter(list, new Predicate() {  
					public boolean evaluate(Object o) {  
						if(((MpmPrescricaoProcedimento)o).getPrescricaoProcedimento() == null) {								
							return true;  
						}
						return false;  
					}  
				});
			}
			
			this.ordenarListaDeItemPrescricaoMedica(list);
			for (MpmPrescricaoProcedimento procedimento : list) {
				this.verificaHierarquiaProcedimentos(prescricaoMedicaVO, procedimento, listOriginal, false);
			}
		}
	}

	private void verificaHierarquiaProcedimentos(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoProcedimento procedimento, List<MpmPrescricaoProcedimento> listaCompleta, Boolean isHierarquico) {
		
		final MpmPrescricaoProcedimentoId id = new MpmPrescricaoProcedimentoId(procedimento.getId().getAtdSeq(), procedimento.getId().getSeq());
		listaCompleta.remove(procedimento);

		ItemPrescricaoMedicaVO item = new ItemPrescricaoMedicaVO();
		item.setAtendimentoSeq(procedimento.getId().getAtdSeq());
		item.setItemSeq(Long.valueOf(procedimento.getId().getSeq()));
		item.setTipo(PrescricaoMedicaTypes.PROCEDIMENTO);				
		item.setDescricao(procedimento.getDescricaoFormatada());
		item.setDthrInicio(procedimento.getDthrInicio());
		item.setDthrFim(procedimento.getDthrFim());
		item.setCriadoEm(procedimento.getCriadoEm());
		item.setAlteradoEm(procedimento.getAlteradoEm());
		item.setSituacao(procedimento.getIndPendente());
		item.setServidorValidacao(procedimento.getServidorValidacao());
		item.setServidorValidaMovimentacao(procedimento.getServidorValidaMovimentacao());
		item.setHierarquico(isHierarquico);
		prescricaoMedicaVO.addItem(item);

		MpmPrescricaoProcedimento prescricaoAlterada = (MpmPrescricaoProcedimento)CollectionUtils.find(listaCompleta, new Predicate() {  
			public boolean evaluate(Object o) {  
				if(((MpmPrescricaoProcedimento)o).getPrescricaoProcedimento() != null && ((MpmPrescricaoProcedimento)o).getPrescricaoProcedimento().getId().equals(id)) {								
					return true;  
				}
				return false;  
			}  
		});

		if(prescricaoAlterada != null) {
			this.verificaHierarquiaProcedimentos(prescricaoMedicaVO, prescricaoAlterada, listaCompleta, true);	
		}
	}

	/**
	 * Orderna lista pela descricaoFormatada
	 * 
	 * @param list
	 */
	private void ordenarListaDeItemPrescricaoMedica(List<? extends ItemPrescricaoMedica> list) {
		Collections.sort(list, new Comparator<ItemPrescricaoMedica>() {
			@Override
			public int compare(ItemPrescricaoMedica item1, ItemPrescricaoMedica item2) {
				return item1.getDescricaoFormatada().compareTo(item2.getDescricaoFormatada());
			}
		});
	}

	/**
	 * rcorvalao 22/09/2010
	 * 
	 * @author mtocchetto
	 * @param prescricaoMedicaVO
	 * @param umFilter
	 * @throws ApplicationBusinessException
	 */
	private void populaNutricaoParental(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoMedicaId prescricaoMedicaId, Boolean listarTodas) throws ApplicationBusinessException {
		try {
			MpmPrescricaoMedicaDAO prescricaoMedicaDAO = getPrescricaoMedicaDAO();
			MpmPrescricaoNptDAO prescricaoNptsDAO = getPrescricaoNptsDAO();

			MpmPrescricaoMedica prescricaoMedica = prescricaoMedicaDAO.obterPorChavePrimaria(prescricaoMedicaId);
			Date dthrFimPme = prescricaoMedica.getDthrFim();

			// MpmkVariaveis mpmkVariaveis = new MpmkVariaveis();
			List<MpmPrescricaoNpt> listaPrescricaoNpts = prescricaoNptsDAO.pesquisarPrescricaoNptPorPME(prescricaoMedicaId, dthrFimPme, listarTodas);
			for (MpmPrescricaoNpt mpmPrescricaoNpt : listaPrescricaoNpts) {
				prescricaoNptsDAO.refresh(mpmPrescricaoNpt);
			}
			
			List<MpmPrescricaoNpt> listaPrescricaoNptsOriginal = (List<MpmPrescricaoNpt>)(new ArrayList<MpmPrescricaoNpt>(listaPrescricaoNpts)).clone();
			if(listarTodas) {
				CollectionUtils.filter(listaPrescricaoNpts, new Predicate() {  
					public boolean evaluate(Object o) {  
						if(((MpmPrescricaoNpt)o).getPrescricaoNpts() == null) {								
							return true;  
						}
						return false;  
					}  
				});
			}

			
			// C IN C_PRCR_NPT (p_atd_seq, p_dthr_inicio_pme, p_dthr_fim_pme)
			for (MpmPrescricaoNpt prescricaoNpts : listaPrescricaoNpts) {
				this.verificaHierarquiaNutricaoParental(prescricaoMedicaVO, prescricaoNpts, listaPrescricaoNptsOriginal, false);
			}
		} catch (Exception e) {
			logError(e.getMessage(), e);
			ManterPrescricaoMedicaExceptionCode.ERRO_MONTAR_LISTA_NUTRICAO_PARENTERAL.throwException(e);
		}
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String descricaoFormatadaNpt(MpmPrescricaoNpt prescricaoNpts) {
		MpmComposicaoPrescricaoNptDAO composicaoPrescricaoNptsDAO = getComposicaoPrescricaoNptsDAO();
		MpmItemPrescricaoNptDAO itemPrescricaoNptDAO = getItemPrescricaoNptDAO();
		
		StringBuilder sbSintaxeNpt = new StringBuilder();
		boolean adicionaNovaLinha = false;

		List<MpmComposicaoPrescricaoNpt> listaComposicaoPrescricaoNpts = null;
		listaComposicaoPrescricaoNpts = composicaoPrescricaoNptsDAO.pesquisarMpmComposicaoPrescricaoNpts(prescricaoNpts);
		
		// C1 IN C_COMPOS_PRCR_NPT (p_atd_seq, c.seq)
		for (MpmComposicaoPrescricaoNpt composicaoPrescricaoNpts : listaComposicaoPrescricaoNpts) {
			if (adicionaNovaLinha) {
				sbSintaxeNpt.append(SEPARADOR_LINHA);
			}
			adicionaNovaLinha = true;

			sbSintaxeNpt.append(composicaoPrescricaoNpts.getAfaTipoComposicoes().getDescricao());

			List<MpmItemPrescricaoNpt> listaItemPrescricaoNpt = null;
			listaItemPrescricaoNpt = itemPrescricaoNptDAO.pesquisarMpmItemPrescricaoNpt(composicaoPrescricaoNpts);
			
			// C2 IN C_ITEM_PRCR_NPT(p_atd_seq, c.seq, c1.seqp)
			for (MpmItemPrescricaoNpt itemPrescricaoNpt : listaItemPrescricaoNpt) {
				sbSintaxeNpt.append(SEPARADOR_LINHA);
				sbSintaxeNpt.append("   ");
				sbSintaxeNpt.append(itemPrescricaoNpt.getAfaComponenteNpts().getDescricao());
				if (itemPrescricaoNpt.getQtdePrescrita() != null) {
					sbSintaxeNpt.append(" DS=");
					// FIXME Tocchetto ver lógica utilizada para obter o
					// valor formatado de qtde_prescrita na consulta.
					String valorFormatado = AghuNumberFormat.formatarValor(
									itemPrescricaoNpt.getQtdePrescrita(),
									MpmItemPrescricaoNpt.class,
									MpmItemPrescricaoNpt.Fields.QTDE_PRESCRITA.toString()
					);
					sbSintaxeNpt.append(valorFormatado);
				}

				if (itemPrescricaoNpt.getAfaFormaDosagens().getUnidadeMedidaMedicas() != null) {
					sbSintaxeNpt.append(' ');
					sbSintaxeNpt.append(
						itemPrescricaoNpt.getAfaFormaDosagens().getUnidadeMedidaMedicas().getDescricao()
					);
				} else {
					String tprSigla = null;
					if(itemPrescricaoNpt.getAfaComponenteNpts().getAfaMedicamentos().getTipoApresentacaoMedicamento() != null) {
						tprSigla = itemPrescricaoNpt.getAfaComponenteNpts()
							.getAfaMedicamentos()
							.getTipoApresentacaoMedicamento()
							.getSigla();
					}
					sbSintaxeNpt.append(StringUtils.defaultString(tprSigla));
				}
				sbSintaxeNpt.append(';');
			}

			if (composicaoPrescricaoNpts.getVelocidadeAdministracao() != null) {
				// FIXME Tocchetto ver lógica para obter valor formatado
				// de velocidade_administracao na consulta.
				// c1.velocidade_administracao
				BigDecimal velocidadeAdministracao = composicaoPrescricaoNpts.getVelocidadeAdministracao();
				String valorFormatado = AghuNumberFormat.formatarValor(
						velocidadeAdministracao,
						MpmComposicaoPrescricaoNpt.class,
						MpmComposicaoPrescricaoNpt.Fields.VELOCIDADE_ADMINISTRACAO.toString()
				);

				sbSintaxeNpt.append(SEPARADOR_LINHA);
				sbSintaxeNpt.append("   Velocidade de Administração: ");
				sbSintaxeNpt.append(valorFormatado);
				if (composicaoPrescricaoNpts.getAfaTipoVelocAdministracoes() != null) {
					sbSintaxeNpt.append(' ');
					sbSintaxeNpt.append(composicaoPrescricaoNpts.getAfaTipoVelocAdministracoes().getDescricao());
				}
			}
		}

		if (sbSintaxeNpt.length() > 0) {
			sbSintaxeNpt.append(SEPARADOR_LINHA);
		}

		sbSintaxeNpt.append(prescricaoNpts.getSegueGotejoPadrao() ? "Segue gotejo padrão; "
						: "Não segue gotejo padrão; ");

		if (prescricaoNpts.getBombaInfusao()) {
			sbSintaxeNpt.append("BI; ");
			// mpmkVariaveis.nptBombaInfusao = 'S' ;
		} // else {
		// mpmkVariaveis.nptBombaInfusao = 'N';
		// }

		if (StringUtils.isNotBlank(prescricaoNpts.getObservacao())) {
			sbSintaxeNpt.append(' ');
			sbSintaxeNpt.append(prescricaoNpts.getObservacao());
			sbSintaxeNpt.append(';');
		}
		
		return sbSintaxeNpt.toString();
	}
	
	private void verificaHierarquiaNutricaoParental(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoNpt prescricaoNpts, List<MpmPrescricaoNpt> listaCompleta, Boolean isHierarquico) {
		
		final MpmPrescricaoNptId id = new MpmPrescricaoNptId(prescricaoNpts.getId().getAtdSeq(), prescricaoNpts.getId().getSeq());
		listaCompleta.remove(prescricaoNpts);
		
		String sbSintaxeNpt = descricaoFormatadaNpt(prescricaoNpts);

		MpmPrescricaoNptId prescricaoNptsId = prescricaoNpts.getId();
		Integer atdSeq = prescricaoNptsId.getAtdSeq();
		Long seq = prescricaoNptsId.getSeq().longValue();

		ItemPrescricaoMedicaVO itemVO = new ItemPrescricaoMedicaVO();
		itemVO.setAtendimentoSeq(atdSeq);
		itemVO.setItemSeq(seq);
		itemVO.setDescricao(sbSintaxeNpt);
		itemVO.setCriadoEm(prescricaoNpts.getCriadoEm());
		itemVO.setAlteradoEm(prescricaoNpts.getAlteradoEm());
		itemVO.setDthrInicio(prescricaoNpts.getDthrInicio());
		itemVO.setDthrFim(prescricaoNpts.getDthrFim());
		itemVO.setSituacao(prescricaoNpts.getIndPendente());
		itemVO.setServidorValidacao(prescricaoNpts.getServidorValidacao());
		itemVO.setServidorValidaMovimentacao(prescricaoNpts.getServidorValidaMovimentacao());			
		itemVO.setTipo(PrescricaoMedicaTypes.NUTRICAO_PARENTAL);
		itemVO.setHierarquico(isHierarquico);
		prescricaoMedicaVO.addItem(itemVO);

		MpmPrescricaoNpt prescricaoAlterada = (MpmPrescricaoNpt)CollectionUtils.find(listaCompleta, new Predicate() {  
			public boolean evaluate(Object o) {  
				if(((MpmPrescricaoNpt)o).getPrescricaoNpts() != null && ((MpmPrescricaoNpt)o).getPrescricaoNpts().getId().equals(id)) {								
					return true;  
				}
				return false;  
			}  
		});

		if(prescricaoAlterada != null) {
			this.verificaHierarquiaNutricaoParental(prescricaoMedicaVO, prescricaoAlterada, listaCompleta, true);	
		}
		
	}
	
	

	
	/**
	 * rcorvalao 22/09/2010
	 * 
	 * @param prescricaoMedicaVO
	 * @param umFilter
	 */
	private void populaHemoterapia(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoMedicaId prescricaoMedicaId, Boolean listarTodas) {
		List<AbsSolicitacoesHemoterapicas> list = this.getBancoDeSangueFacade().buscaSolicitacoesHemoterapicas(prescricaoMedicaId, listarTodas);
		for (AbsSolicitacoesHemoterapicas absSolicitacoesHemoterapica : list) {
			this.getBancoDeSangueFacade().refreshSolicitacoesHeoterapicas(absSolicitacoesHemoterapica);
			for (AbsItensSolHemoterapicas item : absSolicitacoesHemoterapica.getItensSolHemoterapicas()) {
				this.getBancoDeSangueFacade().refreshAbsItensSolHemoterapicas(item);
			}
		}
		
		List<AbsSolicitacoesHemoterapicas> listOriginal = (List<AbsSolicitacoesHemoterapicas>)(new ArrayList<AbsSolicitacoesHemoterapicas>(list)).clone();
		if(listarTodas) {
			CollectionUtils.filter(list, new Predicate() {  
				public boolean evaluate(Object o) {  
					if(((AbsSolicitacoesHemoterapicas)o).getSolicitacaoHemoterapica() == null) {								
						return true;  
					}
					return false;  
				}  
			});
		}

		this.ordenarListaDeItemPrescricaoMedica(list);
		for (AbsSolicitacoesHemoterapicas ash : list) {
			this.verificaHierarquiaHemoterapias(prescricaoMedicaVO, ash, listOriginal, false);
		}
	}

	private void verificaHierarquiaHemoterapias(PrescricaoMedicaVO prescricaoMedicaVO, AbsSolicitacoesHemoterapicas ash, List<AbsSolicitacoesHemoterapicas> listaCompleta, Boolean isHierarquico) {
		
		final AbsSolicitacoesHemoterapicasId id = new AbsSolicitacoesHemoterapicasId(ash.getId().getAtdSeq(), ash.getId().getSeq());
		listaCompleta.remove(ash);

		ItemPrescricaoMedicaVO item = new ItemPrescricaoMedicaVO();
		item.setItemSeq(Long.valueOf(ash.getId().getSeq()));
		item.setAtendimentoSeq(ash.getId().getAtdSeq());
		item.setTipo(PrescricaoMedicaTypes.HEMOTERAPIA);
		item.setDescricao(ash.getDescricaoFormatada());
		item.setCriadoEm(ash.getCriadoEm());
		item.setAlteradoEm(ash.getAlteradoEm());
		item.setDthrInicio(ash.getDthrSolicitacao());
		item.setDthrFim(ash.getDthrFim());			
		item.setSituacao(ash.getIndPendente());
		item.setServidorValidacao(ash.getServidorValidacao());
		item.setServidorValidaMovimentacao(ash.getServidorValidaMovimentacao());
		item.setHierarquico(isHierarquico);
		prescricaoMedicaVO.addItem(item);

		AbsSolicitacoesHemoterapicas prescricaoAlterada = (AbsSolicitacoesHemoterapicas)CollectionUtils.find(listaCompleta, new Predicate() {  
			public boolean evaluate(Object o) {  
				if(((AbsSolicitacoesHemoterapicas)o).getSolicitacaoHemoterapica() != null && ((AbsSolicitacoesHemoterapicas)o).getSolicitacaoHemoterapica().getId().equals(id)) {								
					return true;  
				}
				return false;  
			}  
		});

		if(prescricaoAlterada != null) {
			this.verificaHierarquiaHemoterapias(prescricaoMedicaVO, prescricaoAlterada, listaCompleta, true);	
		}
	}

	
	/**
	 * Campos utilizados:<br>
	 * mpm_solicitacao_consultorias.tipo ,
	 * mpm_solicitacao_consultorias.ind_urgencia ,
	 * agh_especialidades.nome_especialidade , mpm_solicitacao_consultorias.seq
	 * 
	 * 
	 * rcorvalao 22/09/2010
	 * 
	 * @param prescricaoMedicaVO
	 * @param umFilter
	 */
	private void populaConsultoria(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoMedicaId prescricaoMedicaId, Boolean listarTodas) {

		List<MpmSolicitacaoConsultoria> list = this.getSolicitacaoConsultoriasDAO().buscaSolicitacaoConsultoriaPorPrescricaoMedica(prescricaoMedicaId, listarTodas);
		for (MpmSolicitacaoConsultoria mpmSolicitacaoConsultoria : list) {
			this.getSolicitacaoConsultoriasDAO().refresh(mpmSolicitacaoConsultoria);
		}
		
		List<MpmSolicitacaoConsultoria> listOriginal = (List<MpmSolicitacaoConsultoria>)(new ArrayList<MpmSolicitacaoConsultoria>(list)).clone();
		if(listarTodas) {
			CollectionUtils.filter(list, new Predicate() {  
				public boolean evaluate(Object o) {  
					if(((MpmSolicitacaoConsultoria)o).getSolicitacaoConsultoriaOriginal() == null) {								
						return true;  
					}
					return false;  
				}  
			});
		}
		
		this.ordenarListaDeItemPrescricaoMedica(list);
		for (MpmSolicitacaoConsultoria solicitacaoConsultoria : list) {
			this.verificaHierarquiaConsultorias(prescricaoMedicaVO, solicitacaoConsultoria, listOriginal, false);
		}

	}

	private void verificaHierarquiaConsultorias(PrescricaoMedicaVO prescricaoMedicaVO, MpmSolicitacaoConsultoria solicitacaoConsultoria, List<MpmSolicitacaoConsultoria> listaCompleta, Boolean isHierarquico) {
		
		final MpmSolicitacaoConsultoriaId id = new MpmSolicitacaoConsultoriaId(solicitacaoConsultoria.getId().getAtdSeq(), solicitacaoConsultoria.getId().getSeq());
		listaCompleta.remove(solicitacaoConsultoria);

		ItemPrescricaoMedicaVO item = new ItemPrescricaoMedicaVO();
		item.setDescricao(solicitacaoConsultoria.getDescricaoFormatada());
		item.setCriadoEm(solicitacaoConsultoria.getCriadoEm());
		item.setAlteradoEm(solicitacaoConsultoria.getAlteradoEm());
		item.setDthrInicio(solicitacaoConsultoria.getDthrSolicitada());
		item.setDthrFim(solicitacaoConsultoria.getDthrFim());			
		item.setSituacao(solicitacaoConsultoria.getIndPendente());
		item.setServidorValidacao(solicitacaoConsultoria.getServidorValidacao());
		item.setServidorValidaMovimentacao(solicitacaoConsultoria.getServidorValidaMovimentacao());
		item.setAtendimentoSeq(solicitacaoConsultoria.getId().getAtdSeq());
		item.setItemSeq(Long.valueOf(solicitacaoConsultoria.getId().getSeq()));
		item.setTipo(PrescricaoMedicaTypes.CONSULTORIA);
		item.setHierarquico(isHierarquico);
		prescricaoMedicaVO.addItem(item);

		MpmSolicitacaoConsultoria prescricaoAlterada = (MpmSolicitacaoConsultoria)CollectionUtils.find(listaCompleta, new Predicate() {  
			public boolean evaluate(Object o) {  
				if(((MpmSolicitacaoConsultoria)o).getSolicitacaoConsultoriaOriginal() != null && ((MpmSolicitacaoConsultoria)o).getSolicitacaoConsultoriaOriginal().getId().equals(id)) {								
					return true;  
				}
				return false;  
			}  
		});

		if(prescricaoAlterada != null) {
			this.verificaHierarquiaConsultorias(prescricaoMedicaVO, prescricaoAlterada, listaCompleta, true);	
		}
	}

	/**
	 * rcorvalao 22/09/2010
	 * 
	 * @param prescricaoMedicaVO
	 * @param umFilter
	 */
	private void populaMedicamento(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoMedicaId prescricaoMedicaId, Boolean listarTodas) {
		MpmPrescricaoMedica prescricaoMedica = this.getPrescricaoMedicaDAO().obterPorChavePrimaria(prescricaoMedicaId);
		
		List<MpmPrescricaoMdto> listMedicamentos = this.getMpmPrescricaoMdtoDAO().obterListaMedicamentosPrescritosPelaChavePrescricao(prescricaoMedicaId, prescricaoMedica.getDthrFim(), false, listarTodas);
		for (MpmPrescricaoMdto mpmPrescricaoMdto : listMedicamentos) {
			this.getMpmPrescricaoMdtoDAO().refresh(mpmPrescricaoMdto);
			for (MpmItemPrescricaoMdto itemPrescricaoMdto : mpmPrescricaoMdto.getItensPrescricaoMdtos()) {
				this.getMpmItemPrescricaoMdtoDAO().refresh(itemPrescricaoMdto);
			}
		}
	
		List<MpmPrescricaoMdto> listaMedicamentosOriginal = (List<MpmPrescricaoMdto>)(new ArrayList<MpmPrescricaoMdto>(listMedicamentos)).clone();
		if(listarTodas) {
			CollectionUtils.filter(listMedicamentos, new Predicate() {  
				public boolean evaluate(Object o) {  
					if(((MpmPrescricaoMdto)o).getPrescricaoMdtoOrigem() == null) {								
						return true;  
					}
					return false;  
				}  
			});
		}

		this.ordenarListaDeItemPrescricaoMedica(listMedicamentos);
		for (MpmPrescricaoMdto prescricaoMedicamento : listMedicamentos) {
			this.verificaHierarquiaMedicamentos(prescricaoMedicaVO, prescricaoMedicamento, listaMedicamentosOriginal, false);
		}
	}
	
	private void verificaHierarquiaMedicamentos(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoMdto prescricaoMedicamento, List<MpmPrescricaoMdto> listaCompleta, Boolean isHierarquico) {
		
		final MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId(prescricaoMedicamento.getId().getAtdSeq(), prescricaoMedicamento.getId().getSeq());
		listaCompleta.remove(prescricaoMedicamento);

		ItemPrescricaoMedicaVO item = new ItemPrescricaoMedicaVO();
		item.setPrescricaoMedicamento(prescricaoMedicamento);
		item.setDescricao(prescricaoMedicamento.getDescricaoFormatada());
		item.setCriadoEm(prescricaoMedicamento.getCriadoEm());
		item.setAlteradoEm(prescricaoMedicamento.getAlteradoEm());
		item.setDthrInicio(prescricaoMedicamento.getDthrInicio());
		item.setDthrFim(prescricaoMedicamento.getDthrFim());						
		item.setSituacao(prescricaoMedicamento.getIndPendente());
		item.setServidorValidacao(prescricaoMedicamento.getServidorValidacao());
		item.setServidorValidaMovimentacao(prescricaoMedicamento.getServidorValidaMovimentacao());
		item.setAtendimentoSeq(prescricaoMedicamento.getId().getAtdSeq());
		item.setItemSeq(prescricaoMedicamento.getId().getSeq());
		item.setTipo(prescricaoMedicamento.getTipo());
		item.setHierarquico(isHierarquico);
		
		// #18810
		if (prescricaoMedicamento.getIndPendente().equals(DominioIndPendenteItemPrescricao.R)
				&& prescricaoMedicamento.getPrescricaoMdtoReprescritoOrigem() != null) {
			List<MpmItemPrescricaoMdto> listaItens = prescricaoMedicamento.getItensPrescricaoMdtos();
			if (listaItens != null) {
				for (MpmItemPrescricaoMdto itemPrescricaoMdto : listaItens) {
					if (itemPrescricaoMdto.getJustificativaUsoMedicamento() == null) {
						item.setReprescrito(Boolean.TRUE);
						break;
					} else {
						item.setReprescrito(Boolean.FALSE);
					}
				}
			} else {
				item.setReprescrito(Boolean.FALSE);
			}
		} else {
			item.setReprescrito(Boolean.FALSE);
		}
		
		prescricaoMedicaVO.addItem(item);
		
		preencheDadosAntimicrobianos(prescricaoMedicamento, item);

		MpmPrescricaoMdto prescricaoAlterada = (MpmPrescricaoMdto)CollectionUtils.find(listaCompleta, new Predicate() {  
			public boolean evaluate(Object o) {  
				if(((MpmPrescricaoMdto)o).getPrescricaoMdtoOrigem() != null && ((MpmPrescricaoMdto)o).getPrescricaoMdtoOrigem().getId().equals(id)) {								
					return true;  
				}
				return false;  
			}  
		});

		if(prescricaoAlterada != null) {
			this.verificaHierarquiaMedicamentos(prescricaoMedicaVO, prescricaoAlterada, listaCompleta, true);	
		}
	}
	
	/**
	 * Preenche as informacoes inerentes aos medicamentos antimicrobianos de uso restrito.
	 * @param prescricaoMedicamento
	 * @param item
	 */
	private void preencheDadosAntimicrobianos(MpmPrescricaoMdto prescricaoMedicamento, ItemPrescricaoMedicaVO item) {
		if (prescricaoMedicamento.getIndAntiMicrobiano()) {
			// Quantidade de dias
			Integer tempoDuracao = calculaTempoPrescricaoMedicaInicioTratamento(prescricaoMedicamento);
			item.setDias(tempoDuracao.toString());

			// Dose
			item.setDosagem(calculaDosagem(prescricaoMedicamento));
	
			// Nome Medicamento
			item.setNomeMedicamento(defineNomeMedicamento(prescricaoMedicamento));
	
			// Via de administracao
			item.setVia(viaAdministracao(prescricaoMedicamento));
			// Intervalo
			item.setIntervalo(calculaIntervalo(prescricaoMedicamento));
			// Data fim de Duracao
			// TODO
		}
	}
	
	private String defineNomeMedicamento(MpmPrescricaoMdto prescricaoMedicamento) {
		MpmItemPrescricaoMdto itemPrescricaoMdto = prescricaoMedicamento.getItensPrescricaoMdtos().get(0);
		if (itemPrescricaoMdto.getMedicamento() != null) {
			return itemPrescricaoMdto.getMedicamento().getDescricaoEditada();
		}
		return StringUtils.EMPTY;
	}
	
	private String calculaIntervalo(MpmPrescricaoMdto prescricaoMedicamento) {
		if (StringUtils.isNotBlank(prescricaoMedicamento.getTipoFreqAprazamento().getSintaxe())) {
			return prescricaoMedicamento.getTipoFreqAprazamento().getSintaxeFormatada(prescricaoMedicamento.getFrequencia());
		} else {
			return prescricaoMedicamento.getTipoFreqAprazamento().getDescricao();
		}
	}
	
	private String viaAdministracao(MpmPrescricaoMdto prescricaoMedicamento) {
		if (prescricaoMedicamento.getViaAdministracao() != null){
			return prescricaoMedicamento.getViaAdministracao().getSigla();
		}
		return StringUtils.EMPTY;
	}
	
	private String calculaDosagem(MpmPrescricaoMdto prescricaoMedicamento) {
		MpmItemPrescricaoMdto itemPrescricaoMdto = prescricaoMedicamento.getItensPrescricaoMdtos().get(0);
		AfaFormaDosagem formaDosagem = itemPrescricaoMdto.getFormaDosagem();
		String doseFormatada = itemPrescricaoMdto.getDoseFormatada();
		
		if (StringUtils.isNotBlank(formaDosagem.getDescricaoUnidadeMedidaMedica())) {
			return doseFormatada + " " + formaDosagem.getDescricaoUnidadeMedidaMedica();
		} else if (itemPrescricaoMdto.getMedicamento().getTipoApresentacaoMedicamento() != null) {
			return doseFormatada + " " + itemPrescricaoMdto.getMedicamento().getTipoApresentacaoMedicamento().getSigla();
		}
		return StringUtils.EMPTY;
	}

	private Integer calculaTempoPrescricaoMedicaInicioTratamento(MpmPrescricaoMdto prescricaoMedicamento) {
		Date dtPmeInicioVigencia = prescricaoMedicamento.getPrescricaoMedica().getDtReferencia();
		Date dataInicioTratamento = prescricaoMedicamento.getDthrInicioTratamento();
		Integer tempoDuracao = DateUtil.obterQtdDiasEntreDuasDatas(dtPmeInicioVigencia, dataInicioTratamento);
		if (dataInicioTratamento == null || tempoDuracao < 0) {
			tempoDuracao = 0;
		}
		return tempoDuracao;
	}

	
	private void populaSolucao(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoMedicaId prescricaoMedicaId, Boolean listarTodas) {
		MpmPrescricaoMedica prescricaoMedica = this.getPrescricaoMedicaDAO().obterPorChavePrimaria(prescricaoMedicaId);
		
		List<MpmPrescricaoMdto> listSolucoes = this.getMpmPrescricaoMdtoDAO().obterListaMedicamentosPrescritosPelaChavePrescricao(prescricaoMedicaId, prescricaoMedica.getDthrFim(), true, listarTodas);
		for (MpmPrescricaoMdto mpmPrescricaoMdto : listSolucoes) {
			this.getMpmPrescricaoMdtoDAO().refresh(mpmPrescricaoMdto);
			for (MpmItemPrescricaoMdto itemPrescricaoMdto : mpmPrescricaoMdto.getItensPrescricaoMdtos()) {
				this.getMpmItemPrescricaoMdtoDAO().refresh(itemPrescricaoMdto);
			}
		}
		
		List<MpmPrescricaoMdto> listSolucoesOriginal = (List<MpmPrescricaoMdto>)(new ArrayList<MpmPrescricaoMdto>(listSolucoes)).clone();
		if(listarTodas) {
			CollectionUtils.filter(listSolucoes, new Predicate() {  
				public boolean evaluate(Object o) {  
					if(((MpmPrescricaoMdto)o).getPrescricaoMdtoOrigem() == null) {								
						return true;  
					}
					return false;  
				}  
			});
		}

		this.ordenarListaDeItemPrescricaoMedica(listSolucoes);
		for (MpmPrescricaoMdto prescricaoMedicamento : listSolucoes) {
			this.verificaHierarquiaMedicamentos(prescricaoMedicaVO, prescricaoMedicamento, listSolucoesOriginal, false);
		}
		
	}

	/**
	 * rcorvalao 22/09/2010
	 * 
	 * @author mtocchetto
	 * @param prescricaoMedicaVO
	 * @param umFilter
	 */
	private void populaCuidadoMedico(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoMedicaId prescricaoMedicaId, Boolean listarTodas) {
		MpmPrescricaoMedicaDAO daoPrescricaoMedica = this.getPrescricaoMedicaDAO();
		MpmPrescricaoCuidadoDAO daoCuidadosMedicos = this.getMpmPrescricaoCuidadoDAO();
		MpmPrescricaoMedica mpmPrescricaoMedica = daoPrescricaoMedica.obterPorChavePrimaria(prescricaoMedicaId);

		List<MpmPrescricaoCuidado> listaCuidadosMedicos = daoCuidadosMedicos.pesquisarCuidadosMedicos(mpmPrescricaoMedica.getId(), mpmPrescricaoMedica.getDthrFim(), listarTodas);
		for (MpmPrescricaoCuidado mpmPrescricaoCuidado : listaCuidadosMedicos) {
			daoCuidadosMedicos.refresh(mpmPrescricaoCuidado);
		}
		
		List<MpmPrescricaoCuidado> listaCuidadosMedicosOriginal = (List<MpmPrescricaoCuidado>)(new ArrayList<MpmPrescricaoCuidado>(listaCuidadosMedicos)).clone();

		if(listarTodas) {
			CollectionUtils.filter(listaCuidadosMedicos, new Predicate() {  
				public boolean evaluate(Object o) {  
					if(((MpmPrescricaoCuidado)o).getMpmPrescricaoCuidados() == null) {								
						return true;  
					}
					return false;  
				}  
			});
		}

		this.ordenarListaDeItemPrescricaoMedica(listaCuidadosMedicos);
		for (MpmPrescricaoCuidado cuidadoMedico : listaCuidadosMedicos) {
			this.verificaHierarquiaCuidados(prescricaoMedicaVO, cuidadoMedico, listaCuidadosMedicosOriginal, false);
		}
	}

	private void verificaHierarquiaCuidados(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoCuidado cuidadoMedico, List<MpmPrescricaoCuidado> listaCompleta, Boolean isHierarquico) {
		
		final MpmPrescricaoCuidadoId id = new MpmPrescricaoCuidadoId(cuidadoMedico.getId().getAtdSeq(), cuidadoMedico.getId().getSeq());
		listaCompleta.remove(cuidadoMedico);

		ItemPrescricaoMedicaVO item = new ItemPrescricaoMedicaVO();
		item.setDescricao(cuidadoMedico.getDescricaoFormatada());
		item.setCriadoEm(cuidadoMedico.getCriadoEm());
		item.setAlteradoEm(cuidadoMedico.getAlteradoEm());
		item.setDthrInicio(cuidadoMedico.getDthrInicio());
		item.setDthrFim(cuidadoMedico.getDthrFim());									
		item.setSituacao(cuidadoMedico.getIndPendente());
		item.setServidorValidacao(cuidadoMedico.getServidorValidacao());
		item.setServidorValidaMovimentacao(cuidadoMedico.getServidorValidaMovimentacao());
		item.setAtendimentoSeq(cuidadoMedico.getId().getAtdSeq());
		item.setItemSeq(cuidadoMedico.getId().getSeq());
		item.setTipo(PrescricaoMedicaTypes.CUIDADOS_MEDICOS);
		item.setHierarquico(isHierarquico);
		prescricaoMedicaVO.addItem(item);

		MpmPrescricaoCuidado prescricaoAlterada = (MpmPrescricaoCuidado)CollectionUtils.find(listaCompleta, new Predicate() {  
			public boolean evaluate(Object o) {  
				if(((MpmPrescricaoCuidado)o).getMpmPrescricaoCuidados() != null && ((MpmPrescricaoCuidado)o).getMpmPrescricaoCuidados().getId().equals(id)) {								
					return true;  
				}
				return false;  
			}  
		});

		if(prescricaoAlterada != null) {
			this.verificaHierarquiaCuidados(prescricaoMedicaVO, prescricaoAlterada, listaCompleta, true);	
		}
	}

	
	/**
	 * rcorvalao 22/09/2010 bsoliveira 30/09/2010
	 * 
	 * @param {PrescricaoMedicaVO} prescricaoMedicaVO
	 * @param {MpmPrescricaoMedicaId} prescricaoMedicaId
	 */
	private void populaDieta(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoMedicaId prescricaoMedicaId, Boolean listarTodas) {

		MpmPrescricaoMedica prescricaoMedica = this.getPrescricaoMedicaDAO().obterPorChavePrimaria(prescricaoMedicaId);

		List<MpmPrescricaoDieta> listDietas = this.getMpmPrescricaoDietaDAO().buscaDietaPorPrescricaoMedica(prescricaoMedicaId,prescricaoMedica.getDthrFim(), listarTodas);
		for (MpmPrescricaoDieta dieta: listDietas) {
			this.getMpmPrescricaoDietaDAO().refresh(dieta);			
			for (MpmItemPrescricaoDieta itemdieta : dieta.getItemPrescricaoDieta()){
				this.getMpmItemPrescricaoDietaDAO().refresh(itemdieta);
			}
		}
		
		List<MpmPrescricaoDieta> listDietasOriginal = (List<MpmPrescricaoDieta>)(new ArrayList<MpmPrescricaoDieta>(listDietas)).clone();
		
		if(listarTodas) {
			CollectionUtils.filter(listDietas, new Predicate() {  
				public boolean evaluate(Object o) {  
					if(((MpmPrescricaoDieta)o).getMpmPrescricaoDietas() == null) {								
						return true;  
					}
					return false;  
				}  
			});
		}
		
		this.ordenarListaDeItemPrescricaoMedica(listDietas);
		for (MpmPrescricaoDieta prescricaoDieta : listDietas) {
			this.verificaHierarquiaDietas(prescricaoMedicaVO, prescricaoDieta, listDietasOriginal, false);
		}
	}

	private void verificaHierarquiaDietas(PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoDieta prescricaoDieta, List<MpmPrescricaoDieta> listaCompleta, Boolean isHierarquico) {

		final MpmPrescricaoDietaId id = new MpmPrescricaoDietaId(prescricaoDieta.getId().getAtdSeq(), prescricaoDieta.getId().getSeq());
		listaCompleta.remove(prescricaoDieta);
		
		ItemPrescricaoMedicaVO item = new ItemPrescricaoMedicaVO();
		item.setDescricao(prescricaoDieta.getDescricaoFormatada());
		item.setCriadoEm(prescricaoDieta.getCriadoEm());
		item.setAlteradoEm(prescricaoDieta.getAlteradoEm());
		item.setDthrInicio(prescricaoDieta.getDthrInicio());
		item.setDthrFim(prescricaoDieta.getDthrFim());						
		item.setSituacao(prescricaoDieta.getIndPendente());
		item.setServidorValidacao(prescricaoDieta.getServidorValidacao());
		item.setServidorValidaMovimentacao(prescricaoDieta.getServidorValidaMovimentacao());
		item.setAtendimentoSeq(prescricaoDieta.getId()
				.getAtdSeq());
		item.setItemSeq(Long.valueOf(prescricaoDieta.getId().getSeq()));
		item.setTipo(PrescricaoMedicaTypes.DIETA);
		item.setHierarquico(isHierarquico);
		prescricaoMedicaVO.addItem(item);


		MpmPrescricaoDieta prescricaoAlterada = (MpmPrescricaoDieta)CollectionUtils.find(listaCompleta, new Predicate() {  
			public boolean evaluate(Object o) {  
				if(((MpmPrescricaoDieta)o).getMpmPrescricaoDietas() != null && ((MpmPrescricaoDieta)o).getMpmPrescricaoDietas().getId().equals(id)) {								
					return true;  
				}
				return false;  
			}  
		});

		if(prescricaoAlterada != null) {
			this.verificaHierarquiaDietas(prescricaoMedicaVO, prescricaoAlterada, listaCompleta, true);	
		}
	}
	
	/**
	 * bsoliveira
	 * 
	 * 07/10/2010
	 * 
	 * Verifica se existe algum item na lista de itens da prescrição que devem
	 * ser excluido, caso exista chama o metodo de exclusão e remove o objeto da
	 * lista.
	 * 
	 * @param prescricaoMedica
	 * @param {List<ItemPrescricaoMedicaVO>} itens
	 *  
	 */
	public void excluirSelecionados(MpmPrescricaoMedica prescricaoMedica, List<ItemPrescricaoMedicaVO> itens, String nomeMicrocomputador) throws BaseException {
		if (itens != null) {
			boolean hasItemSelecionado = false;
			for (int i = itens.size() - 1; i >= 0; i--) {
				ItemPrescricaoMedicaVO itemPrescricaoMedicaVO = itens.get(i);

				if (itemPrescricaoMedicaVO.getExcluir() != null
						&& itemPrescricaoMedicaVO.getExcluir().booleanValue()) {

					this.excluirItem(prescricaoMedica, itemPrescricaoMedicaVO, nomeMicrocomputador);
					itens.remove(itemPrescricaoMedicaVO);
					hasItemSelecionado = true;
				}
			}
			if (!hasItemSelecionado) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicaExceptionCode.MSG_ADVERTENCIA_NENHUMA_ITEM_SELECIONADO_PARA_EXCLUSAO);
			}
		}
	}

	/**
	 * mtochetto bsoliveira
	 * 
	 * 07/10/2010
	 * 
	 * Executa ação de exclusão de itens da lista de prescrição.
	 * 
	 * @param {ItemPrescricaoMedicaVO} itemPrescricaoMedicaVO
	 *  
	 */
	private void excluirItem(MpmPrescricaoMedica prescricaoMedica, ItemPrescricaoMedicaVO itemPrescricaoMedicaVO, String nomeMicrocomputador) throws BaseException {
		Integer atendimentoSeq = itemPrescricaoMedicaVO.getAtendimentoSeq();
		Long itemSeq = itemPrescricaoMedicaVO.getItemSeq();
		boolean itemExcluido = false;
		
		switch (itemPrescricaoMedicaVO.getTipo()) {
		case DIETA:
			MpmPrescricaoDieta prescricaoDieta = this
					.getPrescricaoMedicaFacade().obterPrescricaoDieta(
							new MpmPrescricaoDietaId(atendimentoSeq, itemSeq));
			if(prescricaoDieta == null){
				itemExcluido = true;
				break;
			}
			this.getPrescricaoMedicaFacade().excluirPrescricaoDieta(
					prescricaoDieta, nomeMicrocomputador);
			break;
		case CUIDADOS_MEDICOS:
			MpmPrescricaoCuidado prescricaoCuidado = this
					.getPrescricaoMedicaFacade().obterPrescricaoCuidado(
							atendimentoSeq, itemSeq);
			if(prescricaoCuidado == null){
				itemExcluido = true;
				break;
			}
			this.getPrescricaoMedicaFacade().removerPrescricaoCuidado(
					prescricaoCuidado);
			break;
		case MEDICAMENTO:
			MpmPrescricaoMdto prescicaoSolucaoOriginal = this
			.getPrescricaoMedicaFacade().obterPrescricaoMedicamento(
					atendimentoSeq, itemSeq);
			getPrescricaoMedicaFacade().desatachar(prescicaoSolucaoOriginal);
			MpmPrescricaoMdto prescicaoSolucao = this
			.getPrescricaoMedicaFacade().obterPrescricaoMedicamento(
					atendimentoSeq, itemSeq);
			if(prescicaoSolucao == null){
				itemExcluido = true;
				break;
			}
			this.getPrescricaoMedicaFacade().removerPrescricaoMedicamento(
					prescricaoMedica, prescicaoSolucao, nomeMicrocomputador,prescicaoSolucaoOriginal);
			break;			
		case SOLUCAO:
			MpmPrescricaoMdto prescicaoMedicamentoOriginal = this
			.getPrescricaoMedicaFacade().obterPrescricaoMedicamento(
					atendimentoSeq, itemSeq);
			prescricaoMedicaFacade.desatachar(prescicaoMedicamentoOriginal);
			MpmPrescricaoMdto prescicaoMedicamento = this
					.getPrescricaoMedicaFacade().obterPrescricaoMedicamento(
							atendimentoSeq, itemSeq);
			if(prescicaoMedicamento == null){
				itemExcluido = true;
				break;
			}
			this.getPrescricaoMedicaFacade().removerPrescricaoMedicamento(
					prescricaoMedica, prescicaoMedicamento, nomeMicrocomputador,prescicaoMedicamentoOriginal);
			break;
		case CONSULTORIA:
			MpmSolicitacaoConsultoria consultoria = this.getConsultoriaON()
					.obterSolicitacaoConsultoriaPorId(atendimentoSeq, itemSeq.intValue());
			if(consultoria == null){
				itemExcluido = true;
				break;
			}
			//TODO: Obter a classe ConsultoriaON em prescricaoMedicaFacade e chamar a exclusão lá
			this.getConsultoriaON().excluirSolicitacaoConsultoria(consultoria);
			break;
		case HEMOTERAPIA:
			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = this.getBancoDeSangueFacade().obterSolicitacoesHemoterapicas(
					atendimentoSeq, itemSeq.intValue());
			if (solicitacaoHemoterapica == null) {
				itemExcluido = true;
				break;
			}
			this.getPrescricaoMedicaFacade().excluirSolicitacaoHemoterapica(solicitacaoHemoterapica, nomeMicrocomputador);
			break;
		case NUTRICAO_PARENTAL:
			MpmPrescricaoNpt prescricaoNpt = mpmPrescricaoNptDAO.obterNutricaoParentaLPeloId(atendimentoSeq, itemSeq.intValue());
			if(prescricaoNpt == null){
				itemExcluido = true;
				break;
			}
			getPrescricaoNptRN().excluir(prescricaoNpt, nomeMicrocomputador);
			break;
		case PROCEDIMENTO:
			MpmPrescricaoProcedimento prescricaoProcedimento = getMpmPrescricaoProcedimentoDAO().obterProcedimentoPeloId(atendimentoSeq, itemSeq);
			if(prescricaoProcedimento == null){
				itemExcluido = true;
				break;
			}
			getPrescreverProcedimentosEspeciaisON().excluirProcedimento(prescricaoProcedimento);
			break;
		default:
			break;
		}
		
		if(itemExcluido){
			throw new ApplicationBusinessException(ManterPrescricaoMedicaExceptionCode.OPTIMISTIC_LOCK);
		}

	}

	public MpmSumarioAlta obterSumarioAltaSemMotivoAltaPeloAtendimento (Integer atdSeq) {
		return getMpmSumarioAltaDAO().obterSumarioAltaSemMotivoAltaPeloAtendimento(atdSeq);
	}

	public MpmAltaSumario obterAltaSumarioConcluidaPeloAtendimento (Integer apaAtdSeq) {
		return getMpmAltaSumarioDAO().obterAltaSumarioConcluidaPeloAtendimento(apaAtdSeq);
	}

	public MpmAltaSumario obterAltaSumarioConcluidaEAtivo (Integer altanAtdSeq, Integer altanApaSeq, Short seqp) {
		return getMpmAltaSumarioDAO().obterAltaSumarioConcluidaEAtivo(altanAtdSeq, altanApaSeq, seqp);
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void desbloquearAlta(Integer atdSeq, Boolean indRecupera, String nomeMicrocomputador) throws BaseException, Exception {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
//		CÓDIGO LEGADO - NÃO UTILIZADO		
//		List<MpmSumarioAlta> sumariosAlta = this.obterListaSumarioAltaPeloAtendimento(atdSeq);
//		for(MpmSumarioAlta sumarioAlta : sumariosAlta) {
//			sumarioAlta.setDthrAlta(null);
//			sumarioAlta.setMotivoAltaMedica(null);
//			sumarioAlta.setIndNecropsia(null);
//			
//			getPrescricaoMedicaFacade().atualizarSumarioAlta(sumarioAlta);
//		}
		
		MpmAltaSumario altaSumario = this.obterAltaSumarioConcluidaPeloAtendimento(atdSeq);
		if(altaSumario != null) {
			altaSumario = this.obterAltaSumarioConcluidaEAtivo(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
			if(altaSumario != null) {
				altaSumario.setDthrAlta(null);
				altaSumario.setConcluido(DominioIndConcluido.N);
				altaSumario.setEstorno(true);
				altaSumario.setRecuperaVersao(indRecupera);
				altaSumario.setDthrEstorno(new Date());
				altaSumario.setServidorEstorno(servidorLogado);
				getPrescricaoMedicaFacade().atualizarAltaSumario(altaSumario, nomeMicrocomputador);
				
				this.getMpmAltaSumarioDAO().desatachar(altaSumario);
				
				getPrescricaoMedicaFacade().removerAltaMotivo(altaSumario);
				
				getPrescricaoMedicaFacade().removerAltaEstadoPaciente(altaSumario);
				
				getPrescricaoMedicaFacade().removerObtCausaDireta(altaSumario);
				
				getPrescricaoMedicaFacade().removerObtCausaAntecedente(altaSumario);
				
				getPrescricaoMedicaFacade().removerObtOutraCausa(altaSumario);
				
				getPrescricaoMedicaFacade().removerObitoNecropsia(altaSumario);
				
				List<MpmPrescricaoMdto> medicamentosOriginal = getMpmPrescricaoMdtoDAO().buscarPrescricaoMedicamentosSemItemRecomendaAltaPeloAtendimento(altaSumario.getId().getApaAtdSeq());
				List<MpmPrescricaoMdto> medicamentos = getMpmPrescricaoMdtoDAO().buscarPrescricaoMedicamentosSemItemRecomendaAltaPeloAtendimento(altaSumario.getId().getApaAtdSeq());
				for(int i =0; i< medicamentos.size() ; i++){//for(MpmPrescricaoMdto medicamento : medicamentos) {
					MpmPrescricaoMdto medicamentoOriginal = medicamentosOriginal.get(i);
					prescricaoMedicaFacade.desatachar(medicamentoOriginal);
					MpmPrescricaoMdto medicamento = medicamentos.get(i);
					
					medicamento.setIndItemRecomendadoAlta(false);
					getPrescricaoMedicaFacade().persistirPrescricaoMedicamento(medicamento, nomeMicrocomputador, medicamentoOriginal);
				}
				
				List<MpmPrescricaoDieta> dietas = getMpmPrescricaoDietaDAO().buscarPrescricaoDietasSemItemRecomendaAltaPeloAtendimento(altaSumario.getId().getApaAtdSeq());
				for(MpmPrescricaoDieta dieta : dietas) {
					dieta.setIndItemRecomendadoAlta(false);
					getPrescricaoMedicaFacade().gravar(dieta, null, null, null, nomeMicrocomputador);
				}

				List<MpmPrescricaoCuidado> cuidados = getMpmPrescricaoCuidadoDAO().buscarPrescricaoCuidadosSemItemRecomendaAltaPeloAtendimento(altaSumario.getId().getApaAtdSeq());
				final Date dataFimVinculoServidor = new Date();
				
				for(MpmPrescricaoCuidado cuidado : cuidados) {
					cuidado.setIndItemRecomendadoAlta(false);
					getPrescricaoMedicaFacade().alterarPrescricaoCuidado(cuidado, nomeMicrocomputador, dataFimVinculoServidor);
				}

				List<MpmAltaRecomendacao> listaAltaRecomendacao = getAltaRecomendacaoDAO().buscaItensAltaRecomendacaoPrescricaoMedica(altaSumario);
				for(MpmAltaRecomendacao altaRecomendacaoGravada:listaAltaRecomendacao){
					altaRecomendacaoGravada.setIndSituacao(DominioSituacao.I);
					this.getManterAltaRecomendacaoRN().atualizarAltaRecomendacao(altaRecomendacaoGravada);
				}
			
			}
		}
		
		List<AghDocumento> documentos = getDocumentoON().buscarDocumentosPeloAtendimento(atdSeq);
		for(AghDocumento documento : documentos) {
			if(documento.getTipo().equals(DominioTipoDocumento.SA) || documento.getTipo().equals(DominioTipoDocumento.SO)){
				for(AghVersaoDocumento versao : documento.getAghVersaoDocumentoes()) {
					versao.setSituacao(DominioSituacaoVersaoDocumento.I);
					getDocumentoON().atualizarVersaoDocumento(versao);
				}
			}	
		}
	}
	
	/**          
	 * @return PrescricaoMedicaVO VO utilizado em "Imprimir Contracheque".
	 * @throws ApplicationBusinessException
	 */
	public PrescricaoMedicaVO buscarDadosCabecalhoContraCheque(
			MpmPrescricaoMedica prescricaoMedica, Boolean listaVaziaItens)
			throws BaseException {
		prescricaoMedica = mpmPrescricaoMedicaDAO.obterPorChavePrimaria(prescricaoMedica.getId());
		
		if(listaVaziaItens) {
			throw new ApplicationBusinessException(ManterPrescricaoMedicaExceptionCode.MENSAGEM_NAO_EXISTEM_ITENS_CONTRACHEQUE);
		}
		
		if(prescricaoMedica==null) {
			throw new ApplicationBusinessException(ManterPrescricaoMedicaExceptionCode.MPM_02319);
		}
		else {
			if(prescricaoMedica.getSituacao()==DominioSituacaoPrescricao.U) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicaExceptionCode.MPM_02320);
			}
			if(prescricaoMedica.getDthrInicioMvtoPendente()!=null) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicaExceptionCode.MPM_02321);
			}
		}
		
		PrescricaoMedicaVO prescricaoMedicaVO = new PrescricaoMedicaVO();

		popularDadosCabecalhoPrescricaoMedicaVO(prescricaoMedicaVO,
				prescricaoMedica);

		return prescricaoMedicaVO;
	}

	private ManterPrescricaoMedicaRN getManterPrescricaoMedicaRN() {
		return manterPrescricaoMedicaRN;
	}

	private DocumentoON getDocumentoON() {
		return documentoON;
	}

	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	protected MpmSumarioAltaDAO getMpmSumarioAltaDAO() {
		return mpmSumarioAltaDAO;
	}
	
	protected MpmPrescricaoProcedimentoDAO getMpmPrescricaoProcedimentoDAO() {
		return mpmPrescricaoProcedimentoDAO;
	}

	protected MpmModoUsoPrescProcedDAO getMpmModoUsoPrescProcedsDAO() {
		return mpmModoUsoPrescProcedDAO;
	}

	protected MpmPrescricaoMdtoDAO getMpmPrescricaoMdtoDAO() {
		return mpmPrescricaoMdtoDAO;
	}
	
	protected MpmPrescricaoDietaDAO getMpmPrescricaoDietaDAO() {
		return mpmPrescricaoDietaDAO;
	}
	
	private MpmItemPrescricaoMdtoDAO getMpmItemPrescricaoMdtoDAO() {
		return mpmItemPrescricaoMdtoDAO;
	}
	
	protected MpmItemPrescricaoDietaDAO getMpmItemPrescricaoDietaDAO() {
		return mpmItemPrescricaoDietaDAO;
	}

	protected MpmPrescricaoCuidadoDAO getMpmPrescricaoCuidadoDAO() {
		return mpmPrescricaoCuidadoDAO;
	}

	protected ConsultoriaON getConsultoriaON(){
		return consultoriaON;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}	
	
	private PrescricaoNptRN getPrescricaoNptRN() {
		return prescricaoNptRN;
	}
	
	public List<ItemPrescricaoMedicaVO> buscaListaProcedimentoEspecialPorPrescricaoMedica(MpmPrescricaoMedicaId prescricaoMedicaId) {
		if (prescricaoMedicaId == null || prescricaoMedicaId.getAtdSeq() == null || prescricaoMedicaId.getSeq() == null) {
			throw new IllegalArgumentException(
					"buscaListaProcedimentoEspecialPorPrescricaoMedica: parametros de filtro invalido");
		}

		PrescricaoMedicaVO prescricaoMedicaVO = new PrescricaoMedicaVO();		
		this.populaProcedimento(prescricaoMedicaVO, prescricaoMedicaId, false);
		
		return prescricaoMedicaVO.getItens();
	}
	
	public List<ItemPrescricaoMedicaVO> buscaListaMedicamentoPorPrescricaoMedica(MpmPrescricaoMedicaId prescricaoMedicaId) {
		if (prescricaoMedicaId == null || prescricaoMedicaId.getAtdSeq() == null || prescricaoMedicaId.getSeq() == null) {
			throw new IllegalArgumentException(
					"buscaListaMedicamentoPorPrescricaoMedica: parametros de filtro invalido");
		}

		PrescricaoMedicaVO prescricaoMedicaVO = new PrescricaoMedicaVO();		
		this.populaMedicamento(prescricaoMedicaVO, prescricaoMedicaId, false);
		
		return prescricaoMedicaVO.getItens();
	}

	public Boolean habilitarAltaSumario(Integer atdSeq){
		return this.getPrescricaoMedicaDAO().habilitarAltaSumario(atdSeq);
	}
	
	public PrescreverProcedimentosEspeciaisON getPrescreverProcedimentosEspeciaisON() {
		return prescreverProcedimentosEspeciaisON;
	}

	public boolean existeProcedimentoEspecialPrescrito(Short codigoProcedimento) {
		return getMpmPrescricaoProcedimentoDAO().existePrescricaoComProcedimentoEspecial(codigoProcedimento);
	}

	public boolean existeModoUsoProcedimentoEspecialPrescrito(final Short pedSeq, final Short seqp) {
		return getMpmModoUsoPrescProcedsDAO().existePrescricaoComModoUso(pedSeq, seqp);
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected MpmAltaRecomendacaoDAO getAltaRecomendacaoDAO() {
		return mpmAltaRecomendacaoDAO;
	}
	
	protected ManterAltaRecomendacaoRN getManterAltaRecomendacaoRN() {
		return manterAltaRecomendacaoRN;
	}
	
	
	/**          
	 * @return String "nome paciente".
	 *
	 */
	private String alteraCaixaAltaNome(String nome) {
		String[] nomes = nome.toLowerCase().split(" ");
		StringBuilder nomesBuilder = new StringBuilder();
		for(String nomeArray : nomes) {
			if((nomeArray.length() > 2)) {
				nomesBuilder.append(nomeArray.substring(0,1).toUpperCase().concat(nomeArray.substring(1)));
				nomesBuilder.append(CARACTER_ESPACO);
			}
			else {
				nomesBuilder.append(nomeArray);
				nomesBuilder.append(CARACTER_ESPACO);
			}
		}
				
		return nomesBuilder.toString();
	}
	
}
