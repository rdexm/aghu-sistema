package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.MpmAltaMotivo;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.MpmSumarioAlta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoAltaMedicaDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;


@Stateless
public class MotivoAltaMedicaCRUD extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(MotivoAltaMedicaCRUD.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmMotivoAltaMedicaDAO mpmMotivoAltaMedicaDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8453057314236711829L;


	public enum  MpmMotivoAltaMedicaCRUDExceptionCode implements BusinessExceptionCode {
		MPMT_MAM_BRI, MPM_MAM_UK1,ERRO_REMOVER_MOTIVO_ALTA_MEDICA,MPM_SAL_MAM_FK1,MPM_AMT_MAM_FK1,AIN_TAM_MAM_FK1;
		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
	
	/**
	 * Método responsável pelo count de registros de motivo de alta medica
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @param unidadeFuncional
	 * @return
	 */
	public Long pesquisarMotivoAltaMedicaCount(Integer codigo,
			String descricao,
			String sigla,
			DominioSituacao situacao){

		return this.getMpmMotivoAltaMedicaDAO().pesquisarMotivoAltaMedicaCount(codigo, descricao, sigla, situacao);
	}
	
	public List<MpmMotivoAltaMedica> pesquisarMotivoAltaMedica(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigo,
			String descricao,
			String sigla,
			DominioSituacao situacao){

		return this.getMpmMotivoAltaMedicaDAO().pesquisarMotivoAltaMedica(firstResult, maxResult, orderProperty, asc, codigo, descricao, sigla, situacao);
	}
	
	public MpmMotivoAltaMedica obterMotivoAltaMedicaPeloId(Short seq) {
		return this.getMpmMotivoAltaMedicaDAO().obterPorChavePrimaria(seq);
	}
	
	
	/**
	 * ORADB: Trigger MPMT_PLA_BRD
	 * @param motivoAltaMedica
	 * @param periodo
	 * @throws ApplicationBusinessException
	 */
	public void removerMotivoAltaMedica(Short seq, Integer periodo) throws BaseException{
		final MpmMotivoAltaMedica motivoAltaMedica = getMpmMotivoAltaMedicaDAO().obterMotivoAltaMedicaPeloId(seq);
		preRemoverMotivoAltaMedica(motivoAltaMedica, periodo);
		try {
			this.getMpmMotivoAltaMedicaDAO().removerMotivoAltaMedica(motivoAltaMedica);
		} catch (ApplicationBusinessException e) {
			MpmMotivoAltaMedicaCRUDExceptionCode.ERRO_REMOVER_MOTIVO_ALTA_MEDICA.throwException();
		}
	}

	/**
	 * ORADB: Triggers MPMT_MAM_BRI e MPMT_PLA_BRU
	 * @param motivoAltaMedica
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void persistMotivoAltaMedica(MpmMotivoAltaMedica motivoAltaMedica) throws ApplicationBusinessException {
			
			// ORADB: Procedure mpmk_mam_rn.rn_mamp_ver_ind_comp
			if(motivoAltaMedica.getIndOutros().equals(Boolean.TRUE) && !motivoAltaMedica.getIndExigeComplemento()){
				throw new ApplicationBusinessException (
						MpmMotivoAltaMedicaCRUDExceptionCode.MPMT_MAM_BRI);
			}
			
			motivoAltaMedica.setDescricao(StringUtil.trim(motivoAltaMedica.getDescricao()));
			motivoAltaMedica.setSigla(StringUtil.trim(motivoAltaMedica.getSigla()));

			if (motivoAltaMedica.getSeq() == null) {
				// Pré-inserir
				this.preInserirMotivoAltaMedica(motivoAltaMedica);
				// Inserir
				this.getMpmMotivoAltaMedicaDAO().persistir(motivoAltaMedica);
				this.getMpmMotivoAltaMedicaDAO().flush();
			} else {
				// Pré-atualizar
				this.preAtualizarMotivoAltaMedica(motivoAltaMedica);
				// Atualizar
				this.getMpmMotivoAltaMedicaDAO().atualizar(motivoAltaMedica);
				this.getMpmMotivoAltaMedicaDAO().flush();
			}
		
	}

	private void preInserirMotivoAltaMedica(MpmMotivoAltaMedica motivoAltaMedica) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Regra MPM_MAM_UK1 (Não permite siglas duplicadas no cadastro dos Motivos de Alta Médica)
		Long ocorrenciasSigla =  getMpmMotivoAltaMedicaDAO().pesquisarMotivoAltaMedicaSiglaCount(motivoAltaMedica.getSigla());
		if (ocorrenciasSigla!=null && ocorrenciasSigla > 0){
			MpmMotivoAltaMedicaCRUDExceptionCode.MPM_MAM_UK1.throwException();
		}
		
		// Setar a coluna CRIADO_EM para a data do momento.
		motivoAltaMedica.setCriadoEm(new Date());
		// Seta as colunas SER_MATRICULA e SER_VIN_CODIGO do usuário logado.
		motivoAltaMedica.setServidor(servidorLogado);
	}
	
	private void preAtualizarMotivoAltaMedica(MpmMotivoAltaMedica motivoAltaMedica) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Seta as colunas SER_MATRICULA e SER_VIN_CODIGO do usuário logado.
		motivoAltaMedica.setServidor(servidorLogado);
	}
	
	public void preRemoverMotivoAltaMedica(MpmMotivoAltaMedica motivoAltaMedica, Integer periodo) throws BaseException {
		// ORADB: Procedure mpmk_mam_rn.rn_mamp_ver_delecao
		if(DateUtil.calcularDiasEntreDatas(new Date(), motivoAltaMedica.getCriadoEm()) > periodo){
			throw new ApplicationBusinessException (
					MpmMotivoAltaMedicaCRUDExceptionCode.ERRO_REMOVER_MOTIVO_ALTA_MEDICA);
		}
		this.validaDelecao(motivoAltaMedica);
	}

	
	public MpmMotivoAltaMedicaDAO getMpmMotivoAltaMedicaDAO(){
		return mpmMotivoAltaMedicaDAO;
	}
	
	/**
	 * @ORADB CHK_ANU_TIPO_ITEM_DIETAS
	 * @param tipoDieta
	 * @throws BaseException
	 */
	public void validaDelecao(MpmMotivoAltaMedica motivoAltaMedica) throws BaseException {
		
		if (motivoAltaMedica == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		
		BaseListException erros = new BaseListException();
		
		erros.add(this.existeItemMotivoAltaMedica(motivoAltaMedica, MpmSumarioAlta.class, MpmSumarioAlta.Fields.MOTIVO_ALTA_MEDICA, MpmMotivoAltaMedicaCRUDExceptionCode.MPM_SAL_MAM_FK1));
		erros.add(this.existeItemMotivoAltaMedica(motivoAltaMedica, MpmAltaMotivo.class, MpmAltaMotivo.Fields.MOTIVO_ALTA_MEDICAS, MpmMotivoAltaMedicaCRUDExceptionCode.MPM_AMT_MAM_FK1));
		erros.add(this.existeItemMotivoAltaMedica(motivoAltaMedica, AinTiposAltaMedica.class, AinTiposAltaMedica.Fields.MOTIVO_ALTA_MEDICAS, MpmMotivoAltaMedicaCRUDExceptionCode.AIN_TAM_MAM_FK1));
		
		if (erros.hasException()) {
			throw erros;
		}
		
	}

	private ApplicationBusinessException existeItemMotivoAltaMedica(MpmMotivoAltaMedica motivoAltaMedica, Class class1, Enum field, BusinessExceptionCode exceptionCode) {

		if (motivoAltaMedica == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		final boolean isExisteItem = getMpmMotivoAltaMedicaDAO().existeItemMotivoAltaMedica(motivoAltaMedica, class1, field);
		
		if(isExisteItem){
			return new ApplicationBusinessException(exceptionCode);
		}
		
		return null;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
