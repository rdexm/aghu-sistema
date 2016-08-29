package br.gov.mec.aghu.business.bancosangue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueResultException;

import br.gov.mec.aghu.bancosangue.dao.AbsExameComponenteVisualPrescricaoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsItensSolHemoterapicasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsJustificativaComponenteSanguineoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsProcedHemoterapicoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsProcedHemoterapicosJnDao;
import br.gov.mec.aghu.bancosangue.dao.AbsValidAmostraProcedDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AbsProcedHemoterapicosJn;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.protocolo.business.IProtocoloFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ProcedimentoHemoterapicoRN extends BaseBusiness {


	private static final Log LOG = LogFactory.getLog(ProcedimentoHemoterapicoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;
	
	@Inject
	private AbsItensSolHemoterapicasDAO absItensSolHemoterapicasDAO;
	
	@Inject
	private AbsProcedHemoterapicoDAO absProcedHemoterapicoDAO;
	
	@EJB
	private IProtocoloFacade protocoloFacade;
	
	@Inject
	private AbsExameComponenteVisualPrescricaoDAO absExameComponenteVisualPrescricaoDAO;
	
	@Inject
	private AbsJustificativaComponenteSanguineoDAO absJustificativaComponenteSanguineoDAO;
	
	@Inject
	private AbsValidAmostraProcedDAO absValidAmostraProcedDAO;
	
	@Inject
	private AbsProcedHemoterapicosJnDao absProcedHemoterapicosJnDao;

	private static final long serialVersionUID = 7180343038916577773L;

	private enum EnumProcedimentoHemoterapicoMessageCode implements BusinessExceptionCode {
		MSG_NAO_POSSIVEL_EXCLUIR_EXISTEM_DEPENDENCIAS, MENSAGEM_CODIGO_PROCEDIMENTO_HEMOTERAPICO_DUPLICADO;
	}
	
	/**
	 * @ORADB ABST_PHE_BRI
	 * 
	 * RN1: Seta ser_vin_codigo e ser_matricula de acordo com o usuário logado.
	 * RN2: Seta CRIADO_EM com a data atual.
	 * 
	 * @param procedimento
	 * @throws ApplicationBusinessException  
	 */
	public void preInsertProcedimentoHemoterapico(AbsProcedHemoterapico procedimento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		procedimento.setRapServidores(servidorLogado);
		procedimento.setCriadoEm(new Date());
	}

	/**
	 * @ORADB ABSP_ENFORCE_PHE_RULES
	 * 
	 * RN1: Chamar procedure RN_PHIP_ATU_INSERT
	 * 
	 * @param procedimento
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void posInsertProcedimentoHemoterapico(AbsProcedHemoterapico procedimento) throws ApplicationBusinessException {
		getCadastrosBasicosPrescricaoMedicaFacade().inserirFatProcedHospInternos(
				null, null, null, null,
				procedimento.getCodigo(),
				procedimento.getDescricao(),
				procedimento.getIndSituacao(),
				null, null, null, null);
	}

	/**
	 * @ORADB ABST_PHE_BRU
	 * 
	 * RN1: Se foi alterado IND_SITUACAO, irá atualizar a situação da tabela fat_proced_hosp_internos
	 * RN2: Se foi alterado DESCRICAO, irá atualizar a descrição da tabela fat_proced_hosp_internos
	 * 
	 * @param procedimento
	 * @throws ApplicationBusinessException 
	 */
	public void preUpdateProcedimentoHemoterapico(AbsProcedHemoterapico procedimento) throws BaseException  {
		FatProcedHospInternos phi = getFaturamentoFacade().obterFatProcedHospInternosPorCodigoProcedimentoHemoterapico(procedimento.getCodigo());
		Boolean atualizar = Boolean.FALSE;
		if (phi != null) {
			if (!phi.getSituacao().equals(procedimento.getIndSituacao())) {
				phi.setSituacao(procedimento.getIndSituacao());		
				atualizar = Boolean.TRUE;
			}
			if (!phi.getDescricao().equals(procedimento.getDescricao())) {
				phi.setDescricao(procedimento.getDescricao());
				atualizar = Boolean.TRUE;
			}
		}
		if (atualizar) {
			getFaturamentoFacade().atualizarFatProcedHospInternos(phi);
		}
	}
	
	/**
	 * ABST_PHE_ARU
	 * 
	 * RN1: Se foi alterado algum campo, realizar insert em abs_proced_hemoterapicos_jn.
	 * 
	 * @param procedimento
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 */
	public void posUpdateProcedimentoHemoterapico(AbsProcedHemoterapico procedimento, AbsProcedHemoterapico original) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (isEntidadeAlterada(procedimento, original)) {
			insertProcedimentoHemoterapicoJN(original);
		}
	}
	
	private Boolean isEntidadeAlterada(AbsProcedHemoterapico procedimento, AbsProcedHemoterapico original) {
		Boolean alterado = Boolean.FALSE;
		if (CoreUtil.modificados(procedimento.getCodigo(), original.getCodigo()) ||
				CoreUtil.modificados(procedimento.getDescricao(), original.getDescricao()) ||
				CoreUtil.modificados(procedimento.getIndAmostra(), original.getIndAmostra()) ||
				CoreUtil.modificados(procedimento.getInformacoes(), original.getInformacoes()) ||
				CoreUtil.modificados(procedimento.getIndSituacao(), original.getIndSituacao()) ||
				CoreUtil.modificados(procedimento.getCriadoEm(), original.getCriadoEm()) ||
				CoreUtil.modificados(procedimento.getIndJustificativa(), original.getIndJustificativa()) ||
				CoreUtil.modificados(procedimento.getRapServidores(), original.getRapServidores())) {
			alterado = Boolean.TRUE;
		}
				
		return alterado;
	}

	private void insertProcedimentoHemoterapicoJN(AbsProcedHemoterapico original) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AbsProcedHemoterapicosJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AbsProcedHemoterapicosJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		PropertyUtils.copyProperties(jn, original);
		getAbsProcedHemoterapicosJnDao().persistir(jn);
	}


	/**
	 * @ORADB ABST_PHE_ARD (PRE-DELETE) 
	 * 
	 * RN1: Consulta por PHE_CODIGO nas seguintes tabelas: 
	 * 			-	MPA_CAD_ORD_ITEM_HEMOTERS, 
	 * 			-	ABS_VALID_AMOSTRAS_PROCEDS, 
	 * 			-	ABS_ITENS_SOL_HEMOTERAPICAS, 
	 * 			-	MPA_USO_ORD_ITEM_HEMOTERS, 
	 * 			-	ABS_EXAMES_COMP_VISUAL_PRCR, 
	 * 			-	ABS_JUSTIF_COMPONENTES_SANG. 
	 * 		Se houver algum registro exibir a mensagem:
	 * 		“Não é possivel excluir. Existem dependências associadas a este registro.”
	 * 
	 * @param procedimento
	 * 
	 *  
	 */
	public void preDeleteProcedimentoHemoterapico(AbsProcedHemoterapico procedimento) throws ApplicationBusinessException {
		if (verificarExistemDependencias(procedimento.getCodigo())) {
			throw new ApplicationBusinessException(EnumProcedimentoHemoterapicoMessageCode.MSG_NAO_POSSIVEL_EXCLUIR_EXISTEM_DEPENDENCIAS);
		}
	}

	/**
	 * @ORADB ABST_PHE_ARD (POS-DELETE) 
	 * 
	 * RN1: Se foi alterado algum campo, realizar insert em abs_proced_hemoterapicos_jn.
	 * 
	 * @param procedimento
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void posDeleteProcedimentoHemoterapico(AbsProcedHemoterapico procedimento) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AbsProcedHemoterapicosJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AbsProcedHemoterapicosJn.class, servidorLogado.getUsuario());
		PropertyUtils.copyProperties(jn, procedimento);
		getAbsProcedHemoterapicosJnDao().persistir(jn);
	}

	private Boolean verificarExistemDependencias(String codigoProcedimentoHemoterapico) {
		Boolean existe = Boolean.FALSE;
		
		List<Object> retornos = new ArrayList<Object>();
		retornos.addAll(getProtocoloFacade().pesquisarMpaCadOrdItemHemoterPorCodigoProcedimentoHemoterapico(codigoProcedimentoHemoterapico));
		retornos.addAll(getProtocoloFacade().pesquisarMpaUsoOrdItemHemotersPorPheCodigo(codigoProcedimentoHemoterapico));
		retornos.addAll(getAbsValidAmostraProcedDAO().pesquisarAbsValidAmostraProcedPorPheCodigo(codigoProcedimentoHemoterapico));
		retornos.addAll(getAbsItensSolHemoterapicasDAO().pesquisarAbsItensSolHemoterapicasPorPheCodigo(codigoProcedimentoHemoterapico));
		retornos.addAll(getAbsExameComponenteVisualPrescricaoDAO().buscarExamesComponenteVisualPrescricao(null, codigoProcedimentoHemoterapico));
		retornos.addAll(getAbsJustificativaComponenteSanguineoDAO().pesquisarJustificativaComponenteSanguineosPorCodigoProcedimentoHemoterapico(codigoProcedimentoHemoterapico));
		FatProcedHospInternos fatProcedHospInternos = getFaturamentoFacade().obterFatProcedHospInternosPorCodigoProcedimentoHemoterapico(codigoProcedimentoHemoterapico);
		if(fatProcedHospInternos != null){
			retornos.add(fatProcedHospInternos);
		}
		
		if (retornos != null && !retornos.isEmpty()) {
			existe = Boolean.TRUE;
		}
		
		return existe;
	}
	
	public void verificarExisteProcedimentoHemoterapico(AbsProcedHemoterapico procedimento) throws ApplicationBusinessException {
		try {
			AbsProcedHemoterapico original = getAbsProcedHemoterapicoDAO().obterOriginal(procedimento.getCodigo());
			if (original != null && original.equals(procedimento) &&
					(procedimento.getCriadoEm() == null ||
							!original.getCriadoEm().equals(procedimento.getCriadoEm())) && 
					(procedimento.getRapServidores() == null || 
							!original.getRapServidores().equals(procedimento.getRapServidores()))) {
				throw new ApplicationBusinessException(EnumProcedimentoHemoterapicoMessageCode.MENSAGEM_CODIGO_PROCEDIMENTO_HEMOTERAPICO_DUPLICADO);
			}
		} catch (NonUniqueResultException e) {
			throw new ApplicationBusinessException(EnumProcedimentoHemoterapicoMessageCode.MENSAGEM_CODIGO_PROCEDIMENTO_HEMOTERAPICO_DUPLICADO);
		}
	}
	
	private ICadastrosBasicosPrescricaoMedicaFacade getCadastrosBasicosPrescricaoMedicaFacade() {
		return cadastrosBasicosPrescricaoMedicaFacade;
	}
	
	private IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	private AbsProcedHemoterapicoDAO getAbsProcedHemoterapicoDAO() {
		return absProcedHemoterapicoDAO;
	}
	
	private AbsProcedHemoterapicosJnDao getAbsProcedHemoterapicosJnDao() {
		return absProcedHemoterapicosJnDao;
	}
	
	private AbsValidAmostraProcedDAO getAbsValidAmostraProcedDAO() {
		return absValidAmostraProcedDAO;
	}
	
	private AbsItensSolHemoterapicasDAO getAbsItensSolHemoterapicasDAO() {
		return absItensSolHemoterapicasDAO;
	}

	private AbsExameComponenteVisualPrescricaoDAO getAbsExameComponenteVisualPrescricaoDAO() {
		return absExameComponenteVisualPrescricaoDAO;
	}
	
	private AbsJustificativaComponenteSanguineoDAO getAbsJustificativaComponenteSanguineoDAO() {
		return absJustificativaComponenteSanguineoDAO;
	}
	
	private IProtocoloFacade getProtocoloFacade() {
		return protocoloFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
