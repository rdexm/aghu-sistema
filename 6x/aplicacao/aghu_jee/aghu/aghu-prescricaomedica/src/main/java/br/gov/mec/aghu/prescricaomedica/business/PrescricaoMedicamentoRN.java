package br.gov.mec.aghu.prescricaomedica.business;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioDuracaoCalculo;
import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCalculoDose;
import br.gov.mec.aghu.dominio.DominioUnidadeBaseParametroCalculo;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaCompoGrupoComponenteDAO;
import br.gov.mec.aghu.farmacia.dao.AfaComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaComponenteNptJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoComponenteNptJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoComposicoesDAO;
import br.gov.mec.aghu.model.AfaCompoGrupoComponente;
import br.gov.mec.aghu.model.AfaCompoGrupoComponenteId;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaGrupoComponNptJn;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.action.ManterPrescricaoMedicamentoExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAprazamentoFrequenciasDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescParecerMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaCompoGrupoComponenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaGrupoComponenteNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.AtualizaServidorPrescricaoMedicamentoAlteracaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AtualizaServidorPrescricaoMedicamentoInclusaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteNPTVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicamentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificaAtendimentoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Implementação das packages MPMK_PMD e MPMK_PMD_RN.
 */

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class PrescricaoMedicamentoRN extends BaseBusiness implements Serializable {


@EJB
private PrescricaoMedicaRN prescricaoMedicaRN;

	private static final Log LOG = LogFactory.getLog(PrescricaoMedicamentoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private MpmItemPrescParecerMdtoDAO mpmItemPrescParecerMdtoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private MpmAprazamentoFrequenciasDAO mpmAprazamentoFrequenciasDAO;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;
	
	@Inject
	private AfaGrupoComponenteNptDAO afaGrupoComponenteNptDAO;
	
	@Inject
	private AfaGrupoComponenteNptJnDAO afaGrupoComponenteNptJnDAO;
	
	@Inject
	private AfaCompoGrupoComponenteDAO afaCompoGrupoComponenteDAO;
	
	@Inject
	private AfaComponenteNptDAO afaComponenteNptDAO;
	
	@Inject
	private AfaTipoComposicoesDAO afaTipoComposicoesDAO;
	
	@Inject
	private AfaComponenteNptJnDAO afaComponenteNptJnDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	// TODO Priorizar a implementação das seguintes procedures:
	// MPMK_PMD_RN.RN_PMDP_DEL_UOM_PROT
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6483787609437950032L;

	private enum PrescricaoMedicamentoExceptionCode implements
			BusinessExceptionCode {
		MPA_00892, MPA_00893, MPA_00894, MPM_00070, MPM_00665, MPM_00928, MPM_01076, 
		MPM_01121, MPM_01196, MPM_01379, MPM_01395, RAP_00175, ERRO_GENERICO, AFA_00172, AFA_00173, AFA_00200,
		AFA_00214, ERRO_EXCLUSAO_GRUPO_COMPON_NPTS_JN, ERRO_EXCLUSAO_GRUPO_COMPON_COMPONENTE, ERRO_EXCLUSAO_GRUPO_COMPON_COMPOSICAO,
		ERRO_EXCLUSAO_GRUPO_COMPON_COMPONENTE_JN;
	}



	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	public void flush() {
		getMpmPrescricaoMdtoDAO().flush();
	}
	
	public void excluirPrescricaoMedicamento(
			MpmPrescricaoMdto prescricaoMedicamento) throws ApplicationBusinessException {
		// Chamada de trigger "before each row"
		this.preExcluir(prescricaoMedicamento);
		
		getMpmPrescricaoMdtoDAO().remover(prescricaoMedicamento);
	}
	
	public void inserirPrescricaoMedicamentoModeloBasico(
			MpmPrescricaoMdto prescricaoMedicamento, String nomeMicrocomputador) throws BaseException {
		// Chamada de trigger "before each row"
		this.preInserir(prescricaoMedicamento, nomeMicrocomputador, new Date(), false);
		
		getMpmPrescricaoMdtoDAO().persistir(prescricaoMedicamento);
		getMpmPrescricaoMdtoDAO().flush();
		
		// Chamada de trigger "after statement" (enforce)
		this.enforcePrescricaoMedicamentoInclusao(prescricaoMedicamento);
		this.enforcePrescricaoMedicamento(prescricaoMedicamento);
	}

	public void inserirPrescricaoMedicamento(
			MpmPrescricaoMdto prescricaoMedicamento, String nomeMicrocomputador, Boolean isCopiado) throws BaseException {
		// Chamada de trigger "before each row"
		this.preInserir(prescricaoMedicamento, nomeMicrocomputador, new Date(), isCopiado);
		
		getMpmPrescricaoMdtoDAO().persistir(prescricaoMedicamento);
		
		// Chamada de trigger "after statement" (enforce)
		this.enforcePrescricaoMedicamentoInclusao(prescricaoMedicamento);
		this.enforcePrescricaoMedicamento(prescricaoMedicamento);
	}

	public MpmPrescricaoMdto ajustaIndPendente(MpmPrescricaoMdto prescricaoMedicamento) throws ApplicationBusinessException{
		return ajustaIndPendente(prescricaoMedicamento, null);
	}
	
	public List<MpmItemPrescricaoMdto> clonarItensPrescricaoMedicamento(MpmPrescricaoMdto prescricaoMedicamento) throws ApplicationBusinessException {
		List<MpmItemPrescricaoMdto> lista = new ArrayList<MpmItemPrescricaoMdto>();
		try {			
			for(MpmItemPrescricaoMdto item : prescricaoMedicamento.getItensPrescricaoMdtos()) {
				MpmItemPrescricaoMdto novoItem = new MpmItemPrescricaoMdto();
				copiarPropriedades(novoItem, item);
				novoItem.setId(null);
				lista.add(novoItem);
			}
			return lista;
		} catch( Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.ERRO_GENERICO);
		}
	}
	
	public MpmPrescricaoMdto ajustaIndPendente(MpmPrescricaoMdto prescricaoMedicamento, List<MpmItemPrescricaoMdto> listaItens) throws ApplicationBusinessException{
		MpmPrescricaoMdto novaPrescricaoMedicamento = new MpmPrescricaoMdto();
		try {
			copiarPropriedades(novaPrescricaoMedicamento, prescricaoMedicamento);
			novaPrescricaoMedicamento.setId(new MpmPrescricaoMdtoId(prescricaoMedicamento.getPrescricaoMedica().getId().getAtdSeq(), null));
			novaPrescricaoMedicamento.setIndPendente(DominioIndPendenteItemPrescricao.P);
			novaPrescricaoMedicamento.setDthrInicio(isPrescricaoVigente(novaPrescricaoMedicamento.getPrescricaoMedica().getDthrInicio(),
					novaPrescricaoMedicamento.getPrescricaoMedica().getDthrFim()) ? novaPrescricaoMedicamento.getPrescricaoMedica().getDthrMovimento() : novaPrescricaoMedicamento.getPrescricaoMedica().getDthrInicio());
			novaPrescricaoMedicamento.setDthrFim(novaPrescricaoMedicamento.getPrescricaoMedica().getDthrFim());
			novaPrescricaoMedicamento.setIndItemRecomendadoAlta(false);
			novaPrescricaoMedicamento.setServidorMovimentado(null);
			novaPrescricaoMedicamento.setServidorValidacao(null);
			novaPrescricaoMedicamento.setDthrValida(null);
			novaPrescricaoMedicamento.setPrescricaoMdtoOrigem(prescricaoMedicamento);
			List<MpmItemPrescricaoMdto> itens = new ArrayList<MpmItemPrescricaoMdto>(0);
			
			if(listaItens != null && listaItens.size() > 0) {
				for(int index = 0; index < listaItens.size(); index++) {
					MpmItemPrescricaoMdto novoItem = new MpmItemPrescricaoMdto();
					copiarPropriedades(novoItem, listaItens.get(index));
					novoItem.setId(null);
					novoItem.setPrescricaoMedicamento(novaPrescricaoMedicamento);
					itens.add(novoItem);
				}				
			}
			else {
				for(int index = 0; index < prescricaoMedicamento.getItensPrescricaoMdtos().size(); index++) {
					MpmItemPrescricaoMdto novoItem = new MpmItemPrescricaoMdto();
					copiarPropriedades(novoItem, prescricaoMedicamento.getItensPrescricaoMdtos().get(index));
					novoItem.setId(null);
					novoItem.setPrescricaoMedicamento(novaPrescricaoMedicamento);
					itens.add(novoItem);
				}
			}
			novaPrescricaoMedicamento.setItensPrescricaoMdtos(itens);
			
			return novaPrescricaoMedicamento;
		} catch( Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.ERRO_GENERICO);
		}
	}
	
	public void atualizarPrescricaoMedicamento(
			MpmPrescricaoMdto prescricaoMedicamento, String nomeMicrocomputador,MpmPrescricaoMdto prescricaoMedicamentoOriginal, Boolean autoExcluirProcedimentoMedicamentoSol) throws BaseException {
		// Chamada de trigger "before each row"
		this.preAtualizar(prescricaoMedicamento, nomeMicrocomputador, new Date(), prescricaoMedicamentoOriginal);
		
		if (!getMpmPrescricaoMdtoDAO().contains(prescricaoMedicamento) && autoExcluirProcedimentoMedicamentoSol == null) {
			getMpmPrescricaoMdtoDAO().merge(prescricaoMedicamento);
		}
	
		// Chamada de trigger "after statement" (enforce)
		this.enforcePrescricaoMedicamento(prescricaoMedicamento);
	}

	public void inserirParecerItemPrescricaoMedicamento(
			MpmItemPrescParecerMdto itemParecer) throws ApplicationBusinessException {
		
		// TODO: Implementar trigger MPMT_IPR_BRI da tabela MPM_ITEM_PRESC_PARECER_MDTO	
        if (getMpmItemPrescParecerMdtoDAO().obterPorChavePrimaria(itemParecer.getId()) != null) {
            getMpmItemPrescParecerMdtoDAO().merge(itemParecer);
        } else {
            getMpmItemPrescParecerMdtoDAO().persistir(itemParecer);
        }

		getMpmItemPrescParecerMdtoDAO().flush();

	}
	

	public void alterarPrescricaoMedicamento(
			MpmPrescricaoMdto prescricaoMedicamento) throws ApplicationBusinessException {
		
		// TODO Auto-generated method stub
		if (!getMpmPrescricaoMdtoDAO().contains(prescricaoMedicamento)) {
			getMpmPrescricaoMdtoDAO().merge(prescricaoMedicamento);
		}
		
		getMpmPrescricaoMdtoDAO().flush();
	}

	public boolean isPrescricaoVigente(Date dthrInicio, Date dthrFim)
	throws ApplicationBusinessException {
		Long dthrAtualTime = new Date().getTime();
		
		if (dthrAtualTime >= dthrInicio.getTime()
				&& dthrAtualTime < dthrFim.getTime()) {
			return true;
		} else if (dthrAtualTime < dthrInicio.getTime()) {
			return false;
		} else {
			throw new ApplicationBusinessException(
					ManterPrescricaoMedicamentoExceptionCode.PERIODO_PRESCRICAO_MEDICAMENTO_INVALIDO,
					DATE_FORMAT.format(dthrInicio), DATE_FORMAT
							.format(dthrInicio));
		}
	}

	protected void copiarPropriedades(Object destino, Object origem)
	throws IllegalAccessException, InvocationTargetException,
	NoSuchMethodException {
		Class clazz = destino.getClass();
		
		// Obs.: Não usar BeanUtils.copyProperties, pois aplica conversoes sobre
		// as propriedades copiadas.
		PropertyUtils.copyProperties(destino, origem);
		
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Class fieldClazz = field.getType();
			if (Collection.class.isAssignableFrom(fieldClazz)) {
				field.setAccessible(true);
				field.set(destino, null);
			}
		}
	}

	
	/**
	 * ORADB Trigger MPMT_PMD_BRU (operacao UPD)
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void preAtualizar(MpmPrescricaoMdto prescricaoMdtos, String nomeMicrocomputador, final Date dataFimVinculoServidor,MpmPrescricaoMdto prescricaoMdtosOriginal)
	throws BaseException {

		/* Verifica vigência do atendimento */
		verificaAtendimento(prescricaoMdtos.getDthrInicio(), 
				  prescricaoMdtos.getDthrFim(),
				  prescricaoMdtos.getPrescricaoMedica().getId().getAtdSeq(),
				  null, null, null);

		/*PrescricaoMedicamentoVO prescricaoMedicamentoVO = new PrescricaoMedicamentoVO();
		prescricaoMedicamentoVO = getMpmPrescricaoMdtoDAO().obtemPrescricaoMedicmanetoVO(prescricaoMdtos.getPrescricaoMedica().getId().getAtdSeq(), prescricaoMdtos.getId().getSeq());*/
		
		if(!CoreUtil.igual(prescricaoMdtosOriginal.getDthrFim(), prescricaoMdtos.getDthrFim()) && prescricaoMdtos.getDthrFim() != null) {
			atualizaPrescricaoMedicaMovimentoPendente(prescricaoMdtos.getPrescricaoMedica().getId().getAtdSeq(), prescricaoMdtos.getDthrInicio(),
					prescricaoMdtos.getDthrFim(), prescricaoMdtos.getAlteradoEm(), prescricaoMdtos.getIndPendente(),"U", nomeMicrocomputador, dataFimVinculoServidor);		
		}
		
		//Se informado Tipo Velocidade Administracao,
		//este deve ter ind_situacao = 'A'  (ativo)
		//linha original ==> if(!CoreUtil.igual((prescricaoMdtos.getTipoVelocAdministracao() != null)?prescricaoMdtos.getTipoVelocAdministracao().getSeq():null, prescricaoMdtosOriginal.getTipoVelcAdmSeq())){
		//O atributo getTipoVelcAdmSeq()  nunca era setado, sempre null; Migração, sendo adequado mas mantendo a lógica para futuras cmparações
		if(!CoreUtil.igual((prescricaoMdtos.getTipoVelocAdministracao() != null)?prescricaoMdtos.getTipoVelocAdministracao().getSeq():null, null)){
			verificaVelocidadePrescricamMedicamentos(prescricaoMdtos.getTipoVelocAdministracao().getSeq());
		}
	
		//Ao informar dthr_fim de uma Prescricao Mdto,
		//atualizar o ser_vin_codigo e ser_matricula de
		//alteracao.
		if(!CoreUtil.igual(prescricaoMdtosOriginal.getDthrFim(), prescricaoMdtos.getDthrFim()) && prescricaoMdtos.getDthrFim() != null) {
			AtualizaServidorPrescricaoMedicamentoAlteracaoVO atualizaServidorVO = atualizaServidorPrescricaoMedicamentoAlteracao();			
			RapServidoresId idServidor = new RapServidoresId(atualizaServidorVO
					.getMatricula(), atualizaServidorVO.getVinCodigo());
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(idServidor);
			prescricaoMdtos.setServidorMovimentado(servidor);

		}

		//Quando um mdto represcrito for excluido e dthr_fim = dthr_inicio
		//atualizar o ser_vin_codigo e ser_matricula de
		//alteracao.
		if(DominioIndPendenteItemPrescricao.Y.equals(prescricaoMdtos.getIndPendente()) && prescricaoMdtos.getDthrFim().equals(prescricaoMdtos.getDthrInicio())) {
			AtualizaServidorPrescricaoMedicamentoAlteracaoVO atualizaServidorVO = atualizaServidorPrescricaoMedicamentoAlteracao();	
			RapServidoresId idServidor = new RapServidoresId(atualizaServidorVO
					.getMatricula(), atualizaServidorVO.getVinCodigo());
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(idServidor);
			prescricaoMdtos.setServidorMovimentado(servidor);
		}
		
		//Quando ind_pendente passar de Y para N atualizar o servidor que validou a
		//movimentacao
		if(DominioIndPendenteItemPrescricao.Y.equals(prescricaoMdtosOriginal.getIndPendente()) && DominioIndPendenteItemPrescricao.N.equals(prescricaoMdtos.getIndPendente())) {
			AtualizaServidorPrescricaoMedicamentoAlteracaoVO atualizaServidorVO = atualizaServidorPrescricaoMedicamentoAlteracao();
			RapServidoresId idServidor = new RapServidoresId(atualizaServidorVO
					.getMatricula(), atualizaServidorVO.getVinCodigo());
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(idServidor);
			prescricaoMdtos.setServidorValidaMovimentacao(servidor);
		}
		
		//Se Tipo Frequencia Aprazamento possuir  ind_digita_frequencia  = 'S'
		//frequencia não pode ser nula  e  Se Tipo Frequencia Aprazamento
		//possuir ind_digita_frequencia  = 'N'  frequencia  deve ser nula
		if(!CoreUtil.igual(prescricaoMdtosOriginal.getTipoFreqAprazamento().getSeq()/*getTipoFreqAprzSeq()*/, (prescricaoMdtos.getTipoFreqAprazamento() != null)?prescricaoMdtos.getTipoFreqAprazamento().getSeq():null) || !CoreUtil.igual(prescricaoMdtosOriginal.getFrequencia(), prescricaoMdtos.getFrequencia())) {
			verificaDigitaFrequenciaAprazamento(prescricaoMdtos.getTipoFreqAprazamento().getSeq(), prescricaoMdtos.getFrequencia());		
		}
		
		//Se informado duracao trat solicitado,
		//atualizar dthr inicio tratamento com dthr_inicio
		if(prescricaoMdtos.getDuracaoTratSolicitado() != null && prescricaoMdtos.getDthrInicioTratamento() == null) {
			prescricaoMdtos.setDthrInicioTratamento(atualizaInicioTratamento(prescricaoMdtos.getDthrInicio(), prescricaoMdtos.getHoraInicioAdministracao()));	
		}
		
		//verifica gotejo e velocidade
		verificaVelocidadeGotejoPrescricaoMedicamentos((prescricaoMdtos.getTipoVelocAdministracao()!= null)?prescricaoMdtos.getTipoVelocAdministracao().getSeq():null, prescricaoMdtos.getGotejo());
		
		//substituicao da lógica de alterado_em gerada pelo headstart
		if (!CoreUtil.igual(prescricaoMdtos.getDthrFim(),
				prescricaoMdtosOriginal.getDthrFim())
				&& prescricaoMdtos.getDthrFim() != null) {
			if (DominioIndPendenteItemPrescricao.N.equals(prescricaoMdtos
					.getIndPendente())
					&& DominioIndPendenteItemPrescricao.Y
							.equals(prescricaoMdtosOriginal.getIndPendente())) {
				return;

			} else if (DominioIndPendenteItemPrescricao.P
					.equals(prescricaoMdtos.getIndPendente())
					&& DominioIndPendenteItemPrescricao.P
							.equals(prescricaoMdtosOriginal.getIndPendente())) {
				return;

			} else {
				prescricaoMdtos.setAlteradoEm(new Date());
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_ATU_SER_ALT
	 * 
	 * @throws ApplicationBusinessException
	 */
	public AtualizaServidorPrescricaoMedicamentoAlteracaoVO atualizaServidorPrescricaoMedicamentoAlteracao() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null || servidorLogado.getId() == null) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.RAP_00175);
		}
		
		AtualizaServidorPrescricaoMedicamentoAlteracaoVO vo = new AtualizaServidorPrescricaoMedicamentoAlteracaoVO();
		vo.setMatricula(servidorLogado.getId().getMatricula());
		vo.setVinCodigo(servidorLogado.getId().getVinCodigo());
		
		return vo;
	}

	/**
	 * ORADB Trigger MPMT_PMD_BRD (operacao INS)
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void preExcluir(MpmPrescricaoMdto prescricaoMdtos)
			throws ApplicationBusinessException {
		
		PrescricaoMedicamentoVO prescricaoMedicamentoVO = getMpmPrescricaoMdtoDAO().obtemPrescricaoMedicmanetoVO(
				prescricaoMdtos.getPrescricaoMedica().getId().getAtdSeq(), prescricaoMdtos.getId().getSeq());
		
		if(prescricaoMedicamentoVO != null && DominioIndPendenteItemPrescricao.N.equals(prescricaoMedicamentoVO.getIndPendente())) {
			verificaDelecao();	
		}
	}
	/**
	 * ORADB Trigger MPMT_PMD_BRI (operacao INS)
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void preInserir(MpmPrescricaoMdto prescricaoMdtos, String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean isCopiado)
			throws BaseException {
		prescricaoMdtos.setCriadoEm(new Date());
		
		if (prescricaoMdtos.getIndItemRecTransferencia() == null){
			prescricaoMdtos.setIndItemRecTransferencia(false);
		}
		
		if (prescricaoMdtos.getIndBombaInfusao() == null){
			prescricaoMdtos.setIndBombaInfusao(false);
		}
		
		verificaAtendimento(prescricaoMdtos.getDthrInicio(), 
														  prescricaoMdtos.getDthrFim(),
														  prescricaoMdtos.getPrescricaoMedica().getId().getAtdSeq(),
														  null, null, null);
		
		//Para inserir um registro na entidade Prescrição Medicamentos é necessário existir um registro  
		//equivalente na entidade Pres-crição Médica (deve se encaixar na validade de uma Prescrição Médica)
		verificaPrescricaoMedica(prescricaoMdtos.getPrescricaoMedica().getId().getAtdSeq(),
							prescricaoMdtos.getDthrInicio(), 
							prescricaoMdtos.getDthrFim(),
							prescricaoMdtos.getCriadoEm(),
							prescricaoMdtos.getIndPendente(),
							"I",
							nomeMicrocomputador, 
							dataFimVinculoServidor);
		
		//Se informado duracao trat solicitado, atualizar dthr inicio tratamento com dthr_inicio.
		if (prescricaoMdtos.getDuracaoTratSolicitado() != null && 
				prescricaoMdtos.getDthrInicioTratamento() == null){
			prescricaoMdtos.setDthrInicioTratamento(
					atualizaInicioTratamento(prescricaoMdtos.getDthrInicio(),
							prescricaoMdtos.getHoraInicioAdministracao()));
		}
		
		//Se Tipo Frequencia Aprazamento possuir ind_digita_frequencia = 'S' frequencia não pode ser nula e  
		//Se Tipo Frequencia Aprazamento possuir ind_digita_frequencia = 'N' frequencia deve ser nula
		if (!isCopiado){
			verificaDigitaFrequenciaAprazamento(prescricaoMdtos.getTipoFreqAprazamento().getSeq(), 
					prescricaoMdtos.getFrequencia());			
		}

		// Ao inserir uma Prescricao Mdto, atualizar o ser_vin_codigo e
		// ser_matricula de inclusão
		AtualizaServidorPrescricaoMedicamentoInclusaoVO atualizaServidorVO = atualizaServidorPrescricaoMedicamentoInclusao();

		RapServidoresId idServidor = new RapServidoresId(atualizaServidorVO
				.getMatricula(), atualizaServidorVO.getVinCodigo());

		RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(idServidor);
		prescricaoMdtos.setServidor(servidor);

		//verifica gotejo e velocidade
		if (!isCopiado){
			verificaVelocidadeGotejoPrescricaoMedicamentos(prescricaoMdtos
					.getTipoVelocAdministracao() != null ? prescricaoMdtos
							.getTipoVelocAdministracao().getSeq() : null, prescricaoMdtos
							.getGotejo());			
		}
	}
	
	/**
	 * Método que implementa a enforce MPMP_ENFORCE_PMD_RULES para o evento de
	 * 'INSERT'
	 * 
	 * ORADB MPMP_ENFORCE_PMD_RULES
	 * 
	 * @param MpmPrescricaoMdto
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void enforcePrescricaoMedicamentoInclusao(MpmPrescricaoMdto prescricaoMdtos)
			throws ApplicationBusinessException {
		if (prescricaoMdtos.getPrescricaoMedica().getId().getAtdSeq() != null 
				&& prescricaoMdtos.getId().getSeq() != null) {
			verificaAutoRelacionamentoPrescricaoMedicamentos(
					prescricaoMdtos.getPrescricaoMedica().getId().getAtdSeq(),
					prescricaoMdtos.getId().getSeq(),
					prescricaoMdtos.getIndPendente());
		}
	}

	/**
	 * Método que implementa a enforce MPMP_ENFORCE_PMD_RULES para o evento diferente
	 * de 'INSERT'
	 * 
	 * ORADB MPMP_ENFORCE_PMD_RULES
	 * 
	 * @param MpmPrescricaoMdto
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void enforcePrescricaoMedicamento(MpmPrescricaoMdto prescricaoMdtos)
			throws ApplicationBusinessException {

		PrescricaoMedicamentoVO prescricaoMedicamentoVO = getMpmPrescricaoMdtoDAO().obtemPrescricaoMedicmanetoVO(prescricaoMdtos.getPrescricaoMedica().getId().getAtdSeq(), prescricaoMdtos.getId().getSeq());

		if(prescricaoMedicamentoVO != null) {
			if(CoreUtil.modificados(prescricaoMedicamentoVO.getDthrInicio(), prescricaoMdtos.getDthrInicio())
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getAtdSeq(), prescricaoMdtos.getPrescricaoMedica().getId().getAtdSeq())
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getSeq(), prescricaoMdtos.getId().getSeq())
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getPmdAtdSeq(), (prescricaoMdtos.getPrescricaoMdtoOrigem() != null)?prescricaoMdtos.getPrescricaoMdtoOrigem().getPrescricaoMedica().getId().getAtdSeq():null)
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getTipoVelcAdmSeq(), (prescricaoMdtos.getTipoVelocAdministracao() != null)?prescricaoMdtos.getTipoVelocAdministracao().getSeq():null)
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getTipoFreqAprzSeq(), (prescricaoMdtos.getTipoFreqAprazamento() != null)?prescricaoMdtos.getTipoFreqAprazamento().getSeq():null)
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getVad(), (prescricaoMdtos.getViaAdministracao() != null)?prescricaoMdtos.getViaAdministracao().getSigla():null)
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getIndSeNecessario(), prescricaoMdtos.getIndSeNecessario())
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getFrequencia(), prescricaoMdtos.getFrequencia())
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getHoraInicioAdministracao(), prescricaoMdtos.getHoraInicioAdministracao())
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getObservacao(), prescricaoMdtos.getObservacao())
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getGotejo(), prescricaoMdtos.getGotejo())
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getQtdeHorasCorrer(), prescricaoMdtos.getQtdeHorasCorrer())
					|| CoreUtil.modificados(prescricaoMedicamentoVO.getDuracaoTratSolicitado(), prescricaoMdtos.getDuracaoTratSolicitado()))
			{
				verificaAtualizacaoPrescricaoMedicamentos(prescricaoMedicamentoVO.getAtdSeq(), prescricaoMedicamentoVO.getPmdSeq());	
			}
		}
	
	}

	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_VER_DELECAO
	 * 
	 * Não permite deleção quando ind_pendente = 'N'
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDelecao() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(
				PrescricaoMedicamentoExceptionCode.MPM_01395);
	}

	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_ATU_INI_TRAT
	 * 
	 * Se informado duração do tratamento solicitado, atualizar data/hora inicio
	 * do tratamento.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Date atualizaInicioTratamento(Date dataHoraInicio,
			Date dataHoraInicioAdm) throws ApplicationBusinessException {
		// #52026
		/*Date dataRetorno = null;
		if (dataHoraInicioAdm != null) {
			Calendar calendarDataHoraInicio = Calendar.getInstance();
			calendarDataHoraInicio.setTime(dataHoraInicio);
			Calendar calendarDataHoraInicioAdm = Calendar.getInstance();
			calendarDataHoraInicioAdm.setTime(dataHoraInicioAdm);

			calendarDataHoraInicio.set(Calendar.HOUR_OF_DAY, calendarDataHoraInicioAdm.get(Calendar.HOUR_OF_DAY));
			calendarDataHoraInicio.set(Calendar.MINUTE, calendarDataHoraInicioAdm.get(Calendar.MINUTE));
			calendarDataHoraInicio.set(Calendar.SECOND, calendarDataHoraInicioAdm.get(Calendar.SECOND));
			calendarDataHoraInicio.set(Calendar.MILLISECOND, calendarDataHoraInicioAdm.get(Calendar.MILLISECOND));
			
	    	dataRetorno = calendarDataHoraInicio.getTime();
			if (dataRetorno.getTime() < dataHoraInicio.getTime()) {
				calendarDataHoraInicio.add(Calendar.DAY_OF_MONTH, 1);
				dataRetorno = calendarDataHoraInicio.getTime();
			}
		} else {
			dataRetorno = dataHoraInicio;
		}*/
		return DateUtil.obterDataComHoraInical(dataHoraInicio);
	}

	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_ATU_PME_PEND
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizaPrescricaoMedicaMovimentoPendente(
			Integer seqAtendimento, Date dataHoraInicio, Date dataHoraFim,
			Date dataHoraMovimentoPendente, DominioIndPendenteItemPrescricao pendente, String operacao, 
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		// Se ind pendente for != 'n' então deve-se atualizar a dthr inicio mvto
		// pendente da prescrição médica se esta tiver nula
		getPrescricaoMedicaRN().verificaPrescricaoMedicaUpdate(seqAtendimento,
				dataHoraInicio, dataHoraFim, dataHoraMovimentoPendente,
				pendente, operacao, nomeMicrocomputador, dataFimVinculoServidor);		
	}

	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_ATU_SER_INCL
	 * 
	 * Ao inserir uma Prescricao Medicamento, atualizar o vinCodigo e matricula
	 * de inclusão.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public AtualizaServidorPrescricaoMedicamentoInclusaoVO atualizaServidorPrescricaoMedicamentoInclusao()
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final Integer serMatricula = servidorLogado.getId().getMatricula();
		final Short serVinCodigo = servidorLogado.getId().getVinCodigo();

		AtualizaServidorPrescricaoMedicamentoInclusaoVO atualizaServidorPrescricaoMedicamentoInclusaoVO = new AtualizaServidorPrescricaoMedicamentoInclusaoVO();
		atualizaServidorPrescricaoMedicamentoInclusaoVO
				.setMatricula(serMatricula);
		atualizaServidorPrescricaoMedicamentoInclusaoVO
				.setVinCodigo(serVinCodigo);

		if (atualizaServidorPrescricaoMedicamentoInclusaoVO.getMatricula() == null) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.RAP_00175);
		}

		return atualizaServidorPrescricaoMedicamentoInclusaoVO;
	}

	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_VER_ATUALIZA
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaAtualizacaoPrescricaoMedicamentos(
			Integer seqAtendimento, Long seqPrescricaoMedicamento)
			throws ApplicationBusinessException {
		// Enquanto ind pendente = 'P' ou 'B' é possível fazer qualquer tipo de
		// atualização. Se ind pendente = 'N' não tem exclusão e só os atributos
		// dthr início tratamento, ind pendente e dthr fim podem ser alterados.
		MpmPrescricaoMdto mpmPrescricaoMdto = getMpmPrescricaoMdtoDAO()
				.obterPorChavePrimaria(
						new MpmPrescricaoMdtoId(seqAtendimento,
								seqPrescricaoMedicamento));
		
		if (mpmPrescricaoMdto != null) {
			DominioIndPendenteItemPrescricao indPendente = mpmPrescricaoMdto.getIndPendente();
			
			if (DominioIndPendenteItemPrescricao.N.equals(indPendente)
					|| DominioIndPendenteItemPrescricao.E.equals(indPendente)
					|| DominioIndPendenteItemPrescricao.A.equals(indPendente)) {
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.MPM_01196);
			}
		}
	}
	
	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_VER_VEL_GOT
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaVelocidadeGotejoPrescricaoMedicamentos(Short tvaSeq,
			BigDecimal gotejo) throws ApplicationBusinessException {
		if (tvaSeq == null && gotejo != null) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_00665);
		}

		if (tvaSeq != null && gotejo == null) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_00665);
		}
	}

	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_VER_AUTOREL
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaAutoRelacionamentoPrescricaoMedicamentos(Integer seqAtendimento,
			Long seqPrescricaoMedicamento, DominioIndPendenteItemPrescricao pendente)
			throws ApplicationBusinessException {
		// Não pode estar relacionado a outra Prescrição
		// Medicamento(autorelacionamento)
		if ("P".equals(pendente) && seqAtendimento != null
				&& seqPrescricaoMedicamento != null) {

			MpmPrescricaoMdto mpmPrescricaoMdto = getMpmPrescricaoMdtoDAO()
					.obterPorChavePrimaria(
							new MpmPrescricaoMdtoId(seqAtendimento,
									seqPrescricaoMedicamento));

			if (mpmPrescricaoMdto != null) {
				DominioIndPendenteItemPrescricao indPendente = mpmPrescricaoMdto
						.getIndPendente();

				if (DominioIndPendenteItemPrescricao.E.equals(indPendente)
						|| DominioIndPendenteItemPrescricao.B.equals(indPendente)) {
					throw new ApplicationBusinessException(
							PrescricaoMedicamentoExceptionCode.MPM_01076);
				}
			} else {
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.MPM_00070);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_VER_DIG_FREQ
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDigitaFrequenciaAprazamento(
			Short seqTipoFrequenciaAprazamento, Short frequencia)
			throws ApplicationBusinessException {
		// Se Tipo Frequencia Aprazamento possuir ind_digita_frequencia = 'S'
		// frequencia não pode ser nula e Se Tipo Frequencia Aprazamento possuir
		// ind_digita_frequencia = 'N' frequencia deve ser nula.
		getPrescricaoMedicaRN().verificaDigitacaoFrequencia(
				seqTipoFrequenciaAprazamento, frequencia);
	}

	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_VER_VELOC
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaVelocidadePrescricamMedicamentos(Short tvaSeq)
			throws ApplicationBusinessException {
		if(tvaSeq != null) {
			if(!getFarmaciaFacade().isTipoVelocidadeAtiva(tvaSeq)) {
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.MPM_01379);
			}
		}
		
	}

	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_VER_PRCR_MED
	 *
	 * @throws ApplicationBusinessException
	 */
	public void verificaPrescricaoMedica(Integer seqAtendimento,
			Date dataHoraInicio, Date dataHoraFim,
			Date dataHoraMovimentoPendente, DominioIndPendenteItemPrescricao pendente, String operacao, 
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		// Para inserir um registro na entidade Prescrição Medicamentos é
		// necessário existir um registro equivalente na entidade Prescrição
		// Médica (deve se encaixar na validade de uma Prescrição Médica)
		getPrescricaoMedicaRN().verificaPrescricaoMedica(seqAtendimento,
				dataHoraInicio, dataHoraFim, dataHoraMovimentoPendente,
				pendente, operacao, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	/**
	 * ORADB Procedure MPMK_PMD_RN.RN_PMDP_VER_ATEND
	 * 
	 * @throws ApplicationBusinessException
	 */
	public VerificaAtendimentoVO verificaAtendimento(Date dataHoraInicio,
			Date dataHoraFim, Integer seqAtendimento, Integer seqHospitalDia,
			Integer seqInternacao, Integer seqAtendimentoUrgencia)
			throws ApplicationBusinessException {
		return getPrescricaoMedicaRN().verificaAtendimento(dataHoraInicio,
				dataHoraFim, seqAtendimento, seqHospitalDia, seqInternacao,
				seqAtendimentoUrgencia);
	}

	/**
	 * @ORADB Procedure MPMP_CALCULO_DOSE
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public Object[] calculoDose(Short frequencia, Short tfqSeq, BigDecimal qtdParamCalculo, DominioUnidadeBaseParametroCalculo baseParamCalculo, 
			DominioTipoCalculoDose tipoCalculoDose, Integer duracao, DominioDuracaoCalculo unidadeTempo, BigDecimal peso,
			BigDecimal altura, BigDecimal sc) {
		try {	
			BigDecimal dose; 
			BigDecimal doseUnitaria; 
			BigDecimal dose24h;
			MpmTipoFrequenciaAprazamento tipoFreqApraz = new MpmTipoFrequenciaAprazamento();
			Long numero = 0l;
			
			if((tfqSeq == null && DominioTipoCalculoDose.D.equals(tipoCalculoDose)) || qtdParamCalculo == null 
					|| baseParamCalculo == null || tipoCalculoDose == null) {
				return null;
			}
			
			if(tfqSeq != null) {
				tipoFreqApraz = getMpmTipoFrequenciaAprazamentoDAO().obterPorChavePrimaria(tfqSeq);
			}
			
			//CÁLCULOS
			if(Boolean.TRUE.equals(tipoFreqApraz.getIndDigitaFrequencia()) && frequencia == null) {
				return null;
			}
			// Continuo
			if(DominioFormaCalculoAprazamento.C.equals(tipoFreqApraz.getIndFormaAprazamento())) {
				numero = 1l;
			}
			// Intervalo
			if(DominioFormaCalculoAprazamento.I.equals(tipoFreqApraz.getIndFormaAprazamento())) {
				numero = (long) (BigDecimal.valueOf(24).divide((tipoFreqApraz.getFatorConversaoHoras().multiply(BigDecimal.valueOf(frequencia))), 0, RoundingMode.DOWN)).intValue();
			}
			// Vezes ao dia
			if(DominioFormaCalculoAprazamento.V.equals(tipoFreqApraz.getIndFormaAprazamento())) {
				if(tipoFreqApraz.getFatorConversaoHoras().compareTo(BigDecimal.ZERO) > 0) {
					numero = (long) BigDecimal.valueOf(24).multiply(BigDecimal.valueOf(frequencia).divide(tipoFreqApraz.getFatorConversaoHoras(), 0, RoundingMode.DOWN)).intValue();	
				}
				else {
					numero = getMpmAprazamentoFrequenciasDAO().pesquisarQuantidadeTipoFreequencia(tfqSeq);
					if(numero == 0) {
						numero = 1l;
					}
				}
			}
			
			if(DominioUnidadeBaseParametroCalculo.K.equals(baseParamCalculo)) {
				dose = qtdParamCalculo.multiply(peso); 
			}
			else {
				dose = qtdParamCalculo.multiply(sc);
			}
	
			//24 hs
			if(DominioTipoCalculoDose.D.equals(tipoCalculoDose)) {
				dose24h = dose;
				doseUnitaria = dose.divide(BigDecimal.valueOf(numero), 2, RoundingMode.DOWN);
			}
			//unitária
			else if(DominioTipoCalculoDose.U.equals(tipoCalculoDose)) {
				doseUnitaria = dose;
				dose24h = dose.multiply(BigDecimal.valueOf(numero));
			}
			//por minuto
			else if(DominioTipoCalculoDose.M.equals(tipoCalculoDose)) {
				if(DominioDuracaoCalculo.M.equals(unidadeTempo)) {
					doseUnitaria = dose.multiply(BigDecimal.valueOf(duracao));
					dose24h = dose.multiply(BigDecimal.valueOf(numero)).multiply(BigDecimal.valueOf(duracao));
				}
				else if(DominioDuracaoCalculo.H.equals(unidadeTempo)) {
					doseUnitaria = dose.multiply(BigDecimal.valueOf(duracao)).multiply(BigDecimal.valueOf(60));
					dose24h = (dose.multiply(BigDecimal.valueOf(numero)).multiply(BigDecimal.valueOf(duracao))).divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_EVEN);
				}
				else {
					doseUnitaria = null;
					dose24h = null;
				}
			}
			//por hora
			else if(DominioTipoCalculoDose.H.equals(tipoCalculoDose)) {
				if(DominioDuracaoCalculo.M.equals(unidadeTempo)) {
					doseUnitaria = (dose.multiply(BigDecimal.valueOf(duracao))).divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_EVEN);
					dose24h = (dose.multiply(BigDecimal.valueOf(numero)).multiply(BigDecimal.valueOf(duracao))).divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_EVEN);
				}
				else if(DominioDuracaoCalculo.H.equals(unidadeTempo)) {
					doseUnitaria = dose.multiply(BigDecimal.valueOf(duracao));
					dose24h = dose.multiply(BigDecimal.valueOf(numero)).multiply(BigDecimal.valueOf(duracao));
				}
				else {
					doseUnitaria = null;
					dose24h = null;
				}
			}
			else {
				doseUnitaria = null;
				dose24h = null;
			}
			
			if(doseUnitaria != null) {
				doseUnitaria = doseUnitaria.setScale(2, RoundingMode.HALF_EVEN);
			}
	
			if(dose24h != null) {
				dose24h = dose24h.setScale(2, RoundingMode.HALF_EVEN);
			}
	
			return new BigDecimal[] {doseUnitaria, dose24h};
		} catch(Exception e) {
			return null;
		}
	}
	
	public void inserirGrupoComponentes(AfaGrupoComponenteNptVO item){
		if(item.getSeq() != null){
			// AFAT_GCN_ARU
			AfaGrupoComponenteNpt itemJournal = afaGrupoComponenteNptDAO.obterPorChavePrimaria(item.getSeq());
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			final AfaGrupoComponNptJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AfaGrupoComponNptJn.class, servidorLogado.getUsuario());
			journal.setCriadoEm(itemJournal.getCriadoEm());
			journal.setDescricao(itemJournal.getDescricao());
			journal.setIndSituacao(itemJournal.getIndSituacao());
			journal.setObservacao(itemJournal.getObservacao());
			journal.setSeq(itemJournal.getSeq());
			journal.setSerMatricula(itemJournal.getRapServidores().getId().getMatricula());
			journal.setSerVinCodigo(itemJournal.getRapServidores().getId().getVinCodigo());
			
			afaGrupoComponenteNptJnDAO.persistir(journal);
			
			itemJournal.setDescricao(item.getDescricao());
			itemJournal.setIndSituacao(item.getIndSituacao());
			itemJournal.setObservacao(item.getObservacao());
			
			afaGrupoComponenteNptDAO.merge(itemJournal);
		}else{
			// AFAT_GCN_BRI
			RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			
			AfaGrupoComponenteNpt entity = new AfaGrupoComponenteNpt();
			entity.setCriadoEm(item.getCriadoEm());
			entity.setDescricao(item.getDescricao());
			entity.setObservacao(item.getObservacao());
			entity.setIndSituacao(item.getIndSituacao());
			
			entity.setRapServidores(servidor);
			entity.setCriadoEm(new Date());
			afaGrupoComponenteNptDAO.persistir(entity);
		}	
	}
	
	public void inserirCompoGrupoComponentes(AfaCompoGrupoComponente item, AfaTipoComposicoes tipo, AfaGrupoComponenteNptVO grupo) throws ApplicationBusinessException{
		// AFAT_TCG_BRI
		if(DominioSituacao.I.equals(tipo.getIndSituacao())){
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00214);
		}
		if(DominioSituacao.I.equals(grupo.getIndSituacao())){
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00200);
		}
		
		AfaTipoComposicoes tipoEntity = afaTipoComposicoesDAO.obterPorChavePrimaria(tipo.getSeq());
		AfaGrupoComponenteNpt grupoEntity = afaGrupoComponenteNptDAO.obterPorChavePrimaria(grupo.getSeq());
		
		item.setAfaGrupoComponenteNpt(grupoEntity);
		item.setAfaTipoComposicoes(tipoEntity);
		
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		item.setRapServidoresByAfaTcgSerFk1(servidor);
			item.setCriadoEm(new Date());
		afaCompoGrupoComponenteDAO.persistir(item);
		}	
	
	public void alterarCompoGrupoComponentes(AfaCompoGrupoComponenteVO item, AfaTipoComposicoes tic, AfaGrupoComponenteNptVO grupo) throws ApplicationBusinessException{
		// AFAT_TCG_BRU
		if(DominioSituacao.I.equals(tic.getIndSituacao())){
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00214);
	}
		if(DominioSituacao.I.equals(grupo.getIndSituacao())){
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00200);
		}
		AfaCompoGrupoComponenteId id =  new AfaCompoGrupoComponenteId();
		id.setGcnSeq(item.getGcnSeq());
		id.setTicSeq(item.getTicSeq());
	
		AfaCompoGrupoComponente entity = afaCompoGrupoComponenteDAO.obterPorChavePrimaria(id);
		AfaGrupoComponenteNpt entityGrupo = afaGrupoComponenteNptDAO.obterPorChavePrimaria(grupo.getSeq());
		AfaTipoComposicoes entityTipo = afaTipoComposicoesDAO.obterPorChavePrimaria(tic.getSeq());

		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();

		
		AfaCompoGrupoComponenteId idEntidade =  new AfaCompoGrupoComponenteId();
		AfaCompoGrupoComponente entidade =  new AfaCompoGrupoComponente();
		idEntidade.setTicSeq(entityTipo.getSeq());
		idEntidade.setGcnSeq(entityGrupo.getSeq());
		entidade.setId(idEntidade);
		entidade.setAfaTipoComposicoes(entityTipo);
		entidade.setAfaGrupoComponenteNpt(entityGrupo);
		entidade.setRapServidoresByAfaTcgSerFk1(entity.getRapServidoresByAfaTcgSerFk1());
		entidade.setRapServidoresByAfaTcgSerFk2(servidor);
		entidade.setAlteradoEm(new Date());
		entidade.setCriadoEm(entity.getCriadoEm());
		entidade.setIndSituacao(item.getIndSituacao());
		
		afaCompoGrupoComponenteDAO.removerPorId(id);
		
		afaCompoGrupoComponenteDAO.persistir(entidade);
}
	
	
	
	public void verificarDelecao(Date data) throws ApplicationBusinessException {
		AghParametros aghParametro = parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_DIAS_PERM_DEL_AFA);

		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar
					.getInstance().getTime(), data);
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00172);
			}
		} else {
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00173);
		}
	}
	
	public void removerGrupoComponentes(AfaGrupoComponenteNptVO item) throws ApplicationBusinessException{
		// AFAT_GCN_BRD
		verificarDelecao(item.getCriadoEm());

		List<AfaGrupoComponNptJn> lista = afaGrupoComponenteNptJnDAO.listarPorSeqGrupo(item.getSeq());
		if(lista != null){	
			if(lista.size() > 0){
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.ERRO_EXCLUSAO_GRUPO_COMPON_NPTS_JN);
			}
		}
		
		List<AfaComponenteNpt> lista2 = afaComponenteNptDAO.listarPorSeqGrupo(item.getSeq());
		if(lista2 != null){	
			if(lista2.size() > 0){
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.ERRO_EXCLUSAO_GRUPO_COMPON_COMPONENTE);
			}
		}
		
		List<AfaCompoGrupoComponenteVO> lista4 = afaCompoGrupoComponenteDAO.obterListaCompoGrupo(item.getSeq());
		if(lista4 != null){	
			if(lista4.size() > 0){
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.ERRO_EXCLUSAO_GRUPO_COMPON_COMPOSICAO);
			}
		}
		
		List<ComponenteNPTVO> lista3 = afaComponenteNptJnDAO.obterJnPorSeqGrupo(item.getSeq());
		if(lista3 != null){	
			if(lista3.size() > 0){
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.ERRO_EXCLUSAO_GRUPO_COMPON_COMPONENTE_JN);
			}
		}
		
		// AFAT_GCN_ARD
		AfaGrupoComponenteNpt itemJournal = afaGrupoComponenteNptDAO.obterPorChavePrimaria(item.getSeq());
		if(itemJournal != null){
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final AfaGrupoComponNptJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AfaGrupoComponNptJn.class, servidorLogado.getUsuario());
		journal.setCriadoEm(itemJournal.getCriadoEm());
		journal.setDescricao(itemJournal.getDescricao());
		journal.setIndSituacao(itemJournal.getIndSituacao());
		journal.setObservacao(itemJournal.getObservacao());
		journal.setSeq(itemJournal.getSeq());
		journal.setSerMatricula(itemJournal.getRapServidores().getId().getMatricula());
		journal.setSerVinCodigo(itemJournal.getRapServidores().getId().getVinCodigo());
		
		afaGrupoComponenteNptJnDAO.persistir(journal);

			afaGrupoComponenteNptDAO.remover(itemJournal);
	}
	
	}
	
	protected PrescricaoMedicaRN getPrescricaoMedicaRN() {
		return prescricaoMedicaRN;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected MpmItemPrescricaoMdtoDAO getItemPrescricaoMdtosDAO(){
		return mpmItemPrescricaoMdtoDAO;
	}
	
	protected MpmPrescricaoMdtoDAO getMpmPrescricaoMdtoDAO(){
		return mpmPrescricaoMdtoDAO;
	}
	
	protected MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO(){
		return mpmTipoFrequenciaAprazamentoDAO;
	}

	protected MpmItemPrescParecerMdtoDAO getMpmItemPrescParecerMdtoDAO(){
		return mpmItemPrescParecerMdtoDAO;
	}
	
	protected MpmAprazamentoFrequenciasDAO getMpmAprazamentoFrequenciasDAO() {
		return mpmAprazamentoFrequenciasDAO;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}