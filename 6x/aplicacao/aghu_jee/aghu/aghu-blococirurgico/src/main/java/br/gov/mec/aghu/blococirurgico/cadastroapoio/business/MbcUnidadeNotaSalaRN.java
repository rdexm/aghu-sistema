package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcUnidadeNotaSalaDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.model.MbcUnidadeNotaSalaId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcUnidadeNotaSalaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcUnidadeNotaSalaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcUnidadeNotaSalaDAO mbcUnidadeNotaSalaDAO;


	@EJB
	private IAghuFacade iAghuFacade;

	private static final long serialVersionUID = 15559929862915949L;

	private enum MbcUnidadeNotaSalaRNExceptionCode implements BusinessExceptionCode {
		MBC_00465,MBC_00308,MBC_00270,MBC_00272,MBC_00277, MSG_MATRIAL_DEPENDENTE_NOTA, MSG_EQUIPAMENTO_DEPENDENTE_NOTA;
	}

	public void persistirNotaDeSala(MbcUnidadeNotaSala mbcUnidadeNotaSala) throws ApplicationBusinessException {
		if (mbcUnidadeNotaSala.getId()!= null && mbcUnidadeNotaSala.getId().getSeqp() == null) {

			this.executarAntesInserir(mbcUnidadeNotaSala);
			this.getMbcUnidadeNotaSalaDAO().persistir(mbcUnidadeNotaSala);
			this.getMbcUnidadeNotaSalaDAO().flush();

		} else {
			this.executarAntesAtualizar(mbcUnidadeNotaSala);
			
			this.getMbcUnidadeNotaSalaDAO().atualizar(mbcUnidadeNotaSala);
			this.getMbcUnidadeNotaSalaDAO().flush();
		}
	}

	/**
	 * @ORADB MBCT_NOA_BRU
	 */
	private void executarAntesAtualizar(final MbcUnidadeNotaSala notaSala)throws ApplicationBusinessException {
		this.verificarAmbosInformados(notaSala);
		this.verificarUnidadeAtiva(notaSala);
		this.verificarProcedimentoAtivo(notaSala);
		this.verificarEspecialidadeAtiva(notaSala);
		if(notaSala.getAghEspecialidades()==null && notaSala.getMbcProcedimentoCirurgicos()==null && notaSala.getSituacao().isAtivo()){
			verificarOcorrenciasSemEspecialidadeProcedimento(notaSala);
		}		
	}

	/**
	 * @ORADB MBCT_PUC_BRI
	 */
	private void executarAntesInserir(MbcUnidadeNotaSala notaSala) throws ApplicationBusinessException {
		this.verificarAmbosInformados(notaSala);
		this.verificarUnidadeAtiva(notaSala);
		this.verificarProcedimentoAtivo(notaSala);
		this.verificarEspecialidadeAtiva(notaSala);
		if(notaSala.getAghEspecialidades()==null && notaSala.getMbcProcedimentoCirurgicos()==null){
			verificarOcorrenciasSemEspecialidadeProcedimento(notaSala);
		}
		notaSala.setCriadoEm(new Date());
	}

	/**
	 * @ORADB MBC_NOA_CK1
	 */
	private void verificarAmbosInformados(final MbcUnidadeNotaSala notaSala) throws ApplicationBusinessException {
		if(notaSala.getMbcProcedimentoCirurgicos()!=null && notaSala.getAghEspecialidades() != null){
			throw new ApplicationBusinessException(MbcUnidadeNotaSalaRNExceptionCode.MBC_00465);
		}
	}
	
	/**
	 * @ORADB mbck_noa_rn.rn_noap_ver_unid_fun
	 */
	private void verificarUnidadeAtiva(final MbcUnidadeNotaSala notaSala) throws ApplicationBusinessException {
		AghUnidadesFuncionais unf = getAghuFacade().obterUnidadeFuncional(notaSala.getId().getUnfSeq());
		if(!unf.getIndSitUnidFunc().isAtivo()){
			throw new ApplicationBusinessException(MbcUnidadeNotaSalaRNExceptionCode.MBC_00308);
		}
	}
	
	/**
	 * @ORADB mbck_noa_rn.rn_noap_ver_proced
	 */
	private void verificarProcedimentoAtivo(final MbcUnidadeNotaSala notaSala) throws ApplicationBusinessException {
		if(notaSala.getMbcProcedimentoCirurgicos()!=null && !notaSala.getMbcProcedimentoCirurgicos().getIndSituacao().isAtivo()){
			throw new ApplicationBusinessException(MbcUnidadeNotaSalaRNExceptionCode.MBC_00270);
		}
	}
	
	/**
	 * @ORADB mbck_noa_rn.rn_noap_ver_espec
	 */
	private void verificarEspecialidadeAtiva(final MbcUnidadeNotaSala notaSala) throws ApplicationBusinessException {
		if(notaSala.getAghEspecialidades()!=null && !notaSala.getAghEspecialidades().getIndSituacao().isAtivo()){
			throw new ApplicationBusinessException(MbcUnidadeNotaSalaRNExceptionCode.MBC_00272);
		}
	}

	/**
	 * @ORADB mbck_noa_rn.rn_noap_ver_default
	 */
	private void verificarOcorrenciasSemEspecialidadeProcedimento(final MbcUnidadeNotaSala notaSala) throws ApplicationBusinessException {
		List<MbcUnidadeNotaSala> notasSala = getMbcUnidadeNotaSalaDAO().obterNotasSalaPorUnidadeSemEspecialidadeProcedimento(notaSala);
		if(notasSala!=null && notasSala.size() > 0){
			throw new ApplicationBusinessException(MbcUnidadeNotaSalaRNExceptionCode.MBC_00277);
		}
	}

	public void excluir(final MbcUnidadeNotaSalaId mbcUnidadeNotaSalaId) throws ApplicationBusinessException {
		MbcUnidadeNotaSala notaSala = this.getMbcUnidadeNotaSalaDAO().obterPorChavePrimaria(mbcUnidadeNotaSalaId);
		verificarDependencias(notaSala);
		getMbcUnidadeNotaSalaDAO().remover(notaSala);
		getMbcUnidadeNotaSalaDAO().flush();
	}
	
	private void verificarDependencias(final MbcUnidadeNotaSala notaSala) throws ApplicationBusinessException{
		if(notaSala != null && notaSala.getMbcEquipamentoNotaSalaes()!=null && notaSala.getMbcEquipamentoNotaSalaes().size() > 0){
			throw new ApplicationBusinessException(MbcUnidadeNotaSalaRNExceptionCode.MSG_EQUIPAMENTO_DEPENDENTE_NOTA);
		}
		
		if(notaSala != null && notaSala.getMbcMaterialImpNotaSalaUnes()!=null && notaSala.getMbcMaterialImpNotaSalaUnes().size() > 0){
			throw new ApplicationBusinessException(MbcUnidadeNotaSalaRNExceptionCode.MSG_MATRIAL_DEPENDENTE_NOTA);
		}
	}

	protected MbcUnidadeNotaSalaDAO getMbcUnidadeNotaSalaDAO() {
		return mbcUnidadeNotaSalaDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}
}
