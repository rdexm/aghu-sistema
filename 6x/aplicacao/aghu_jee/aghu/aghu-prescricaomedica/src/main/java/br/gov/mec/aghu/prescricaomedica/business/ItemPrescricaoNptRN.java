package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaCompoGrupoComponenteDAO;
import br.gov.mec.aghu.farmacia.dao.AfaComponenteNptDAO;
import br.gov.mec.aghu.model.AfaCompoGrupoComponente;
import br.gov.mec.aghu.model.AfaCompoGrupoComponenteId;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNptId;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNptId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmComposicaoPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ItemPrescricaoNptRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3223350423981462910L;

	private static final Log LOG = LogFactory.getLog(ItemPrescricaoNptRN.class);

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO;
	
	@Inject
	private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;

	@Inject
	private AfaComponenteNptDAO afaComponenteNptDAO;

	@Inject
	private MpmComposicaoPrescricaoNptDAO mpmComposicaoPrescricaoNptDAO;
	
	@Inject
	private AfaCompoGrupoComponenteDAO afaCompoGrupoComponenteDAO; 
	
	protected enum ItemPrescricaoNpExceptionCode implements
	BusinessExceptionCode {
		MPM_01664, MPM_01665, MPM_00428, MPM_01176, MPM_01132, MPM_01131, MPM_01182, MPM_01183;
	}
		
	public void persistir(MpmItemPrescricaoNpt item) throws ApplicationBusinessException {
		MpmItemPrescricaoNpt original = mpmItemPrescricaoNptDAO.obterOriginal(item.getId());
		if(original == null) {
			inserir(item);
		} else {
			atualizar(item);
		}
	}
	
	public void inserir(MpmItemPrescricaoNpt item) throws ApplicationBusinessException {
		this.preInserir(item);
		mpmItemPrescricaoNptDAO.persistir(item);
	}
	
	public void atualizar(MpmItemPrescricaoNpt item) throws ApplicationBusinessException {
		this.preAtualizar(item);
		mpmItemPrescricaoNptDAO.atualizar(item);
	}
	
	//MPMT_PNP_BRU
	protected void preAtualizar(MpmItemPrescricaoNpt item) throws ApplicationBusinessException {
		MpmItemPrescricaoNpt original = mpmItemPrescricaoNptDAO.obterOriginal(item.getId());
		
		/*  COMPONENTE_NPT deve estar ativo e compativel com a idade
		do ATENDIMENTO, se paciente pediatrico ou prematuro COMPONENTE_NPT
		deve ter ind_pediatria = SIM. Se paciente adulto COMPONENTE_NPT deve ter
		ind_adulto = SIM.    */
		if(CoreUtil.modificados(item.getAfaComponenteNpts(), original.getAfaComponenteNpts())) {
			this.rnIpnpCpteNpt(item, "A");
		}
		
		/* FORMA_DOSAGEM deve estar ativa e ser compativel com o COMPONENTE_NPT
		associado */
		if(CoreUtil.modificados(item.getAfaComponenteNpts(), original.getAfaComponenteNpts()) || CoreUtil.modificados(item.getAfaFormaDosagens(), original.getAfaFormaDosagens())) {
			this.rnIpnpVerFrmDos(item, "A");
		}
		
		/* o COMPONENTE_NPT so pode ser associado ao ITEM_PRESCRICAO_NPT se o
		GRUPO_COMPONENTE_NPT estiver associado ao TIPO_COMPOSICAO da compo
		sicao da PRESCRICAO_NPT na entidade COMPOSICAO_GRUPO_COMPONENTE*/
		if(CoreUtil.modificados(item.getAfaComponenteNpts(), original.getAfaComponenteNpts())) {
			this.rnIpnpVerAssoc(item);
		}
		
	}
	
	//MPMT_PNP_BRI	
	protected void preInserir(MpmItemPrescricaoNpt item) throws ApplicationBusinessException {
		/* s¿ posso inserir um item prescricao npt se a prescricao npt n¿estiver validada */
		this.rnIpnpVerInclusao(item);
		
		/*  COMPONENTE_NPT deve estar ativo e compativel com a idade
		do ATENDIMENTO, se paciente pediatrico ou prematuro COMPONENTE_NPT
		deve ter ind_pediatria = SIM. Se paciente adulto COMPONENTE_NPT deve ter
		ind_adulto = SIM.    */
		this.rnIpnpCpteNpt(item, "I");
		
		/* FORMA_DOSAGEM deve estar ativa e ser compativel com o COMPONENTE_NPT
		associado */
		this.rnIpnpVerFrmDos(item, "I");
		
		/* o COMPONENTE_NPT so pode ser associado ao ITEM_PRESCRICAO_NPT se o
		GRUPO_COMPONENTE_NPT estiver associado ao TIPO_COMPOSICAO da compo
		sicao da PRESCRICAO_NPT na entidade COMPOSICAO_GRUPO_COMPONENTE*/
		this.rnIpnpVerAssoc(item);
	}
	
	/**
	 * ORADB RN_IPNP_VER_INCLUSAO
	 */
	protected void rnIpnpVerInclusao(MpmItemPrescricaoNpt item) throws ApplicationBusinessException {
		MpmPrescricaoNpt prescricao = mpmPrescricaoNptDAO.obterPorChavePrimaria(new MpmPrescricaoNptId(item.getId().getCptPnpAtdSeq(), item.getId().getCptPnpSeq().intValue()));
		if(prescricao == null) {
			throw new ApplicationBusinessException(
					ItemPrescricaoNpExceptionCode.MPM_01664);
		}
		
		if(!DominioIndPendenteItemPrescricao.P.equals(prescricao.getIndPendente()) && !DominioIndPendenteItemPrescricao.B.equals(prescricao.getIndPendente())
				&& !DominioIndPendenteItemPrescricao.D.equals(prescricao.getIndPendente())) {
			throw new ApplicationBusinessException(
					ItemPrescricaoNpExceptionCode.MPM_01665);
		}
	}

	/**
	 * ORADB RN_IPNP_VER_CPTE_NPT
	 */
	protected void rnIpnpCpteNpt(MpmItemPrescricaoNpt item, String operacao) throws ApplicationBusinessException {
		AfaComponenteNpt componente = afaComponenteNptDAO.obterPorChavePrimaria(item.getAfaComponenteNpts().getCodigo());
		if(componente == null) {
			throw new ApplicationBusinessException(
					ItemPrescricaoNpExceptionCode.MPM_00428);
		}
		if(!DominioSituacao.A.equals(componente.getIndSituacao()) && StringUtils.equalsIgnoreCase("A", operacao)) {
			throw new ApplicationBusinessException(
					ItemPrescricaoNpExceptionCode.MPM_01176);
		}
	}

	/**
	 * ORADB RN_IPNP_VER_FRM_DOS
	 */
	protected void rnIpnpVerFrmDos(MpmItemPrescricaoNpt item, String operacao) throws ApplicationBusinessException {
		AfaFormaDosagem dosagem = farmaciaFacade.obterAfaFormaDosagem(item.getAfaFormaDosagens().getSeq());
		if(dosagem == null) {
			throw new ApplicationBusinessException(
					ItemPrescricaoNpExceptionCode.MPM_01132);
		}
		if(!DominioSituacao.A.equals(dosagem.getIndSituacao()) && StringUtils.equalsIgnoreCase("A", operacao)) {
			throw new ApplicationBusinessException(
					ItemPrescricaoNpExceptionCode.MPM_01131);
		}
	}

	/**
	 * ORADB RN_IPNP_VER_ASSOC
	 */
	protected void rnIpnpVerAssoc(MpmItemPrescricaoNpt item) throws ApplicationBusinessException {
		MpmComposicaoPrescricaoNpt composicao = mpmComposicaoPrescricaoNptDAO.obterPorChavePrimaria(new MpmComposicaoPrescricaoNptId(item.getId().getCptPnpAtdSeq(), item.getId().getCptPnpSeq(), item.getId().getCptSeqp()));
		AfaComponenteNpt componente = afaComponenteNptDAO.obterPorChavePrimaria(item.getAfaComponenteNpts().getCodigo());
		
		AfaCompoGrupoComponente compoGrupo = afaCompoGrupoComponenteDAO.obterPorChavePrimaria(new AfaCompoGrupoComponenteId(componente.getAfaGrupoComponenteNpt().getSeq(), composicao.getAfaTipoComposicoes().getSeq()));
		
		if(compoGrupo == null) {
			throw new ApplicationBusinessException(
					ItemPrescricaoNpExceptionCode.MPM_01182);
		}
		if(!DominioSituacao.A.equals(compoGrupo.getIndSituacao())) {
			throw new ApplicationBusinessException(
					ItemPrescricaoNpExceptionCode.MPM_01183);
		}
	}

	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
}
