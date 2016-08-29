package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmPacAtendProfissional;
import br.gov.mec.aghu.model.MpmPacAtendProfissionalId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPacAtendProfissionalDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MpmPacAtendProfissionalRN extends BaseBusiness{

	private static final long serialVersionUID = -6850512593852745543L;
	
	protected Log getLogger() {
		return LogFactory.getLog(MpmPacAtendProfissionalRN.class);
	}
	
	@Inject
	private MpmPacAtendProfissionalDAO mpmPacAtendProfissionalDAO;
	
	@EJB
	private PrescreverProcedimentoEspecialRN prescreverProcedimentoEspecialRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	public enum MpmPacAtendProfissionalRNExceptionCode implements BusinessExceptionCode {
		MPM_01001;
	}
	public void persistirPacAtendProfissional(MpmPacAtendProfissional mpmPacAtendProfissional, RapServidores servidorPara) throws ApplicationBusinessException {
		if(mpmPacAtendProfissional.getId() == null){
			inserirPacAtendProfissional(mpmPacAtendProfissional);
		}else {
			atualizarPacAtendProfissional(mpmPacAtendProfissional, servidorPara);
		}
	}
	
	private void inserirPacAtendProfissional(MpmPacAtendProfissional mpmPacAtendProfissional) throws ApplicationBusinessException {
		preInserir(mpmPacAtendProfissional);
		mpmPacAtendProfissionalDAO.persistir(mpmPacAtendProfissional);
	}
	/**
	 * @ORADB 
	 * @param mpmPacAtendProfissional
	 * @throws ApplicationBusinessException 
	 */
	private void preInserir(MpmPacAtendProfissional mpmPacAtendProfissional) throws ApplicationBusinessException {
		mpmPacAtendProfissional.setCriadoEm(new Date());
		mpmPacAtendProfissional.setId(new MpmPacAtendProfissionalId(
				mpmPacAtendProfissional.getAtendimento().getSeq(),
				mpmPacAtendProfissional.getServidor().getId().getMatricula(),
				mpmPacAtendProfissional.getServidor().getId().getVinCodigo()));
	}	
	
	private void atualizarPacAtendProfissional(MpmPacAtendProfissional mpmPacAtendProfissional, RapServidores servidorPara) throws ApplicationBusinessException {
		preAtualizar(mpmPacAtendProfissional);
		
		final Date criadoEm = mpmPacAtendProfissional.getCriadoEm();
		final AghAtendimentos atendimento = mpmPacAtendProfissional.getAtendimento();
		final MpmPacAtendProfissionalId id = mpmPacAtendProfissional.getId();
		
		MpmPacAtendProfissionalId novoId = new MpmPacAtendProfissionalId(id.getAtdSeq(), servidorPara.getId().getMatricula(), servidorPara.getId().getVinCodigo());
		
		if(mpmPacAtendProfissionalDAO.obterPorChavePrimaria(novoId) == null){
			
			mpmPacAtendProfissionalDAO.remover(mpmPacAtendProfissional);
			mpmPacAtendProfissionalDAO.flush();
			
			MpmPacAtendProfissional atualizado =  new MpmPacAtendProfissional();
			
			atualizado.setCriadoEm(criadoEm);
			atualizado.setAtendimento(atendimento);
			atualizado.setServidor(servidorPara);
			atualizado.setId(novoId);
			
			mpmPacAtendProfissionalDAO.persistir(atualizado);
		}
	}
	
	/**
	 * @ORADB MPMT_PAR_BRU
	 * @param mpmPacAtendProfissional
	 * @throws ApplicationBusinessException 
	 */
	private void preAtualizar(MpmPacAtendProfissional mpmPacAtendProfissional) throws ApplicationBusinessException {
		MpmPacAtendProfissional mpmPacAtendProfissionalOriginal = mpmPacAtendProfissionalDAO.obterOriginal(mpmPacAtendProfissional);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		if (CoreUtil.modificados(mpmPacAtendProfissional.getAtendimento()
				.getSeq(), mpmPacAtendProfissionalOriginal.getAtendimento().getSeq())) {
			prescreverProcedimentoEspecialRN.verificaAtendimento(
				mpmPacAtendProfissional.getAtendimento().getDthrInicio(),
				mpmPacAtendProfissional.getAtendimento().getDthrFim(), 
				mpmPacAtendProfissional.getAtendimento().getSeq(), 
				mpmPacAtendProfissional.getAtendimento().getHospitalDia().getSeq(), 
				mpmPacAtendProfissional.getAtendimento().getInternacao().getSeq(),
				mpmPacAtendProfissional.getAtendimento().getAtendimentoUrgencia().getSeq());
		}
		verificaUsuarioConectado(servidorLogado, mpmPacAtendProfissionalOriginal.getServidor());
	}
	
	/**
	 * @ORADB MPMK_PAR_RN.RN_PARP_VER_SERVIDOR
	 * @throws ApplicationBusinessException 
	 */
	private void verificaUsuarioConectado(RapServidores servidorLogado, RapServidores servidorOriginal) throws ApplicationBusinessException {
		if(CoreUtil.modificados(servidorLogado, servidorOriginal)) {
			throw new ApplicationBusinessException(MpmPacAtendProfissionalRNExceptionCode.MPM_01001);
		}
	}
	
	public Integer transferirPacienteEmAcompanhamento(RapServidoresVO profissionalDe, RapServidoresVO profissionalPara) throws ApplicationBusinessException {
		RapServidores servidorDe = registroColaboradorFacade.buscarServidor(profissionalDe.getVinculo(), profissionalDe.getMatricula());
		RapServidores servidorPara = registroColaboradorFacade.buscarServidor(profissionalPara.getVinculo(), profissionalPara.getMatricula());
		List<MpmPacAtendProfissional> profissionais = mpmPacAtendProfissionalDAO.pesquisarAssociacoesPorServidor(servidorDe);
		
		for (MpmPacAtendProfissional mpmPacAtendProfissional : profissionais) {
			atualizarPacAtendProfissional(mpmPacAtendProfissional, servidorPara);
		}
		return profissionais.size();
	}
	
	//#1290 - RN01
	public boolean validarMesmoProfissionalPorVinculoEMatricula(
			RapServidoresVO profissionalDe, RapServidoresVO profissionalPara) {
		
		if (profissionalDe != null && profissionalPara != null
				&& profissionalDe.getMatricula().intValue() == profissionalPara.getMatricula().intValue()
				&& profissionalDe.getVinculo().shortValue() == profissionalPara.getVinculo().shortValue()) {
			return true;
		}
		return false;
	}
	
	public boolean verificaMpmPacAtendProfissionalCadastrado(MpmPacAtendProfissional mpmPacAtendProfissional) {
		MpmPacAtendProfissional mpmPacAtendProfissionalOriginal = mpmPacAtendProfissionalDAO.obterOriginal(mpmPacAtendProfissional);
		return (mpmPacAtendProfissionalOriginal != null ? true : false);
	}
}
