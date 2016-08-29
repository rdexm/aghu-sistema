package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.MptItemPrescricaoMedicamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmUnidadeMedidaMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmUnidadeMedidaMedicaDAO.UnidadeMedidaMedicaCRUDExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmUnidadeMedidaMedicaJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;


@Stateless
public class UnidadeMedidaMedicaCRUD extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(UnidadeMedidaMedicaCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmUnidadeMedidaMedicaDAO mpmUnidadeMedidaMedicaDAO;
	
	@Inject
	private MpmUnidadeMedidaMedicaJnDAO mpmUnidadeMedidaMedicaJnDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2521379050944922863L;



	/**
	 * Método responsável pelo count de registros de planos de pos alta
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @param unidadeFuncional
	 * @return
	 */
	public Long pesquisarUnidadesMedidasCount(Integer codigoUnidade,
			String descricaoUnidade,
			DominioSituacao situacaoUnidade){

		return getMpmUnidadeMedidaMedicaDAO().pesquisarUnidadesMedidaMedicaCount(codigoUnidade, descricaoUnidade, situacaoUnidade);
	}
	
	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedica(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigoMedida,
			String descricaoMedida,
			DominioSituacao situacaoUnidadeMedidaMedica){

		return getMpmUnidadeMedidaMedicaDAO().pesquisarUnidadesMedidaMedica(firstResult, maxResult, orderProperty, asc, codigoMedida, descricaoMedida, situacaoUnidadeMedidaMedica);
	}
	
	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedica(Object idOuDescricao){

		return getMpmUnidadeMedidaMedicaDAO().pesquisarUnidadesMedidaMedica(idOuDescricao);
	}
	
	public void removerUnidadeMedidaMedica(Integer seq, Integer periodo) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final MpmUnidadeMedidaMedica unidade = getMpmUnidadeMedidaMedicaDAO().obterUnidadesMedidaMedicaPeloId(seq);
		unidade.setServidor(servidorLogado);
		preRemoveUnidadeMedidaMedica(unidade, periodo);
		getMpmUnidadeMedidaMedicaDAO().removerUnidade(unidade);
		getMpmUnidadeMedidaMedicaJnDAO().persistirUnidadeMedidaMedicaJn(unidade, DominioOperacoesJournal.DEL, servidorLogado.getUsuario());
	}

	public void persistUnidadeMedidaMedica(MpmUnidadeMedidaMedica unidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		MpmUnidadeMedidaMedica unidadeOLD = getMpmUnidadeMedidaMedicaDAO().obterOriginal(unidade.getSeq());
		
		unidade.setDescricao(StringUtil.trim(unidade.getDescricao()));
		unidade.setServidor(servidorLogado);
		
		if (unidade.getSeq() == null) {
			prePersistUnidadeMedidaMedica(unidade);
			getMpmUnidadeMedidaMedicaDAO().persistir(unidade);
			getMpmUnidadeMedidaMedicaDAO().flush();
			getMpmUnidadeMedidaMedicaJnDAO().persistirUnidadeMedidaMedicaJn(unidade, DominioOperacoesJournal.INS, servidorLogado.getUsuario());
		} else {
			prePersistUnidadeMedidaMedica(unidade);
			//MpmUnidadeMedidaMedica unidadeAux = obterUnidadesMedidaMedicaPeloId(unidade.getSeq());
			if(!unidade.getIndSituacao().isAtivo() &&
					(unidadeOLD.isIndAuxConcentracao()			!=unidade.isIndAuxConcentracao()
					|| unidadeOLD.isIndAuxMonitHemodinamica() 	!=unidade.isIndAuxMonitHemodinamica()
					|| unidadeOLD.isIndAuxPrescricaoDose() 		!=unidade.isIndAuxPrescricaoDose()
					|| unidadeOLD.isIndAuxUsoDialise() 			!=unidade.isIndAuxUsoDialise()
					|| unidadeOLD.isIndAuxUsoNutricao() 		!=unidade.isIndAuxUsoNutricao()
					|| unidadeOLD.isIndAuxVolumeNpt() 			!=unidade.isIndAuxVolumeNpt()
					)
				){
				throw new ApplicationBusinessException (UnidadeMedidaMedicaCRUDExceptionCode.MPM_00775);
			}
			getMpmUnidadeMedidaMedicaDAO().merge(unidade);
			getMpmUnidadeMedidaMedicaDAO().flush();
			getMpmUnidadeMedidaMedicaJnDAO().persistirUnidadeMedidaMedicaJn(unidade, DominioOperacoesJournal.UPD, servidorLogado.getUsuario());
		}
	}

	private MpmUnidadeMedidaMedicaJnDAO getMpmUnidadeMedidaMedicaJnDAO() {
		return mpmUnidadeMedidaMedicaJnDAO;
	}
	
	/**
	 * @ORADB MPMT_UMM_BRI, MPMT_UMM_ARU, MPMT_UMM_BRU
	 * @param unidade
	 * @throws ApplicationBusinessException
	 */
	private void prePersistUnidadeMedidaMedica(MpmUnidadeMedidaMedica unidade) throws ApplicationBusinessException {
		
		//FOI RETIRADO O TRECHO DE CÓDIGO QUE VALIDAVA SE AO MENOS UMA OPÇÃO HAVIA SIDO PREENCHIDA
		//PARA A UNIDADE DE MEDIDA MÉDICA ATIVA COM O OBJETIVO DE ATENDER À MELHORIA #13772
		
		if(unidade.getSeq()==null){
			unidade.setCriadoEm(new Date());
		}
	}
	
	/**
	 * @ORADB MPMT_UMM_ARD
	 * @param unidade
	 * @param periodo
	 * @throws BaseException 
	 */
	private void preRemoveUnidadeMedidaMedica(MpmUnidadeMedidaMedica unidade, Integer periodo) throws BaseException {
		if(DateUtil.calcularDiasEntreDatas(new Date(), unidade.getCriadoEm()) > periodo){
			throw new ApplicationBusinessException (UnidadeMedidaMedicaCRUDExceptionCode.ERRO_REMOVER_UNIDADE_MEDIDA_MEDICA);
		}

		validaDelecao(unidade);
	}
	
	
	/**
	 * @param unidade
	 * @throws BaseException
	 */
	public void validaDelecao(MpmUnidadeMedidaMedica unidade) throws BaseException {
		
		if (unidade == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		
		BaseListException erros = new BaseListException();
		
		erros.add(this.existeRelacionamento(unidade, AfaMedicamento.class, AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS, UnidadeMedidaMedicaCRUDExceptionCode.AFA_MED_UMM_FK1));
		erros.add(this.existeRelacionamento(unidade, AfaFormaDosagem.class, AfaFormaDosagem.Fields.UNIDADE_MEDICAS, UnidadeMedidaMedicaCRUDExceptionCode.AFA_FOR_DOS_FK1));
		erros.add(this.existeRelacionamento(unidade, AnuTipoItemDieta.class, AnuTipoItemDieta.Fields.UNIDADE_MEDIDA_MEDICAS, UnidadeMedidaMedicaCRUDExceptionCode.ANU_TIP_ITE_DIE_FK1));
		erros.add(this.existeRelacionamento(unidade, MpmItemPrescricaoMdto.class, MpmItemPrescricaoMdto.Fields.UNIDADE_MEDIDA_MEDICAS, UnidadeMedidaMedicaCRUDExceptionCode.MPM_ITE_PRE_MDTO_FK1));
		
		erros.add(this.existeRelacionamento(unidade, MpmItemPrescricaoNpt.class, MpmItemPrescricaoNpt.Fields.UNIDADE_MEDIDA_MEDICAS, UnidadeMedidaMedicaCRUDExceptionCode.MPM_ITE_PRE_NPTS_FK1));
		erros.add(this.existeRelacionamento(unidade, MpmTipoModoUsoProcedimento.class, MpmTipoModoUsoProcedimento.Fields.UNIDADE_MEDIDA_MEDICA, UnidadeMedidaMedicaCRUDExceptionCode.MPM_TIP_MOD_USO_PROC_FK1));
		erros.add(this.existeRelacionamento(unidade, MptItemPrescricaoMedicamento.class, MptItemPrescricaoMedicamento.Fields.UNIDADE_MEDIDA_MEDICA, UnidadeMedidaMedicaCRUDExceptionCode.MPT_ITE_PRE_MDTO_FK1));

		if (erros.hasException()) {
			throw new ApplicationBusinessException(UnidadeMedidaMedicaCRUDExceptionCode.ERRO_REMOVER_REGISTROS_ASSOCIADOS_UNIDADE_MEDIDA);
		}
		
	}
	
	/**
	 * 
	 * Verificar a existência de registros de unidade de medida medicas em outras entidades
	 * 
	 * @param pdtSeq
	 * @return
	 * @param unidade
	 * @param class1
	 * @param field
	 * @param exceptionCode
	 * @return
	 */
	private ApplicationBusinessException existeRelacionamento(MpmUnidadeMedidaMedica unidade, Class class1, Enum field, UnidadeMedidaMedicaCRUDExceptionCode exceptionCode) {

		if (unidade == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		final boolean isExisteRelacao = getMpmUnidadeMedidaMedicaDAO().existeRelacionamento(unidade, class1, field);
		
		if(isExisteRelacao){
			return new ApplicationBusinessException(exceptionCode);
		}
		
		return null;
	}

	private MpmUnidadeMedidaMedicaDAO getMpmUnidadeMedidaMedicaDAO() {
		return mpmUnidadeMedidaMedicaDAO;
	}
	
	
	
	/**
	 * Pesquisa as unidades de medidas médicas ativas para concentracao
	 * 
	 * @param idOuDescricao
	 * @return
	 */
	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedicaConcentracao(
			Object idOuDescricao) {
		return getMpmUnidadeMedidaMedicaDAO()
				.pesquisarUnidadesMedidaMedicaConcentracao(idOuDescricao);
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
