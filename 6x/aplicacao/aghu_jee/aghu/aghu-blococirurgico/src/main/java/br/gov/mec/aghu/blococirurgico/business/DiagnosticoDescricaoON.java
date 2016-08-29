package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcDiagnosticoDescricaoDAO;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricao;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricaoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DiagnosticoDescricaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(DiagnosticoDescricaoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDiagnosticoDescricaoDAO mbcDiagnosticoDescricaoDAO;


	@EJB
	private DiagnosticoDescricaoRN diagnosticoDescricaoRN;

	private static final long serialVersionUID = -6979481759400604462L;

	protected enum DiagnosticoDescricaoONExceptionCode implements BusinessExceptionCode {
		DIAGNOSTICO_DESCRICAO_OPERATORIO_DUPLICADO;
	}
	
	public void inserirDiagnosticoDescricoesPreOperatorio( MbcDiagnosticoDescricao diagnosticoDescricao,
														   List<MbcDiagnosticoDescricao> listaPreOperatorio,
														   List<MbcDiagnosticoDescricao> listaPosOperatorio
														 ) throws ApplicationBusinessException {
		
		if(validarDiagnosticoDescricao(listaPreOperatorio,diagnosticoDescricao)){
			throw new ApplicationBusinessException(DiagnosticoDescricaoONExceptionCode.DIAGNOSTICO_DESCRICAO_OPERATORIO_DUPLICADO, diagnosticoDescricao.getCid().getCodigo());
			
		} else {
			final DiagnosticoDescricaoRN rn = getDiagnosticoDescricaoRN();
			rn.inserirDiagnosticoDescricoes(diagnosticoDescricao);
			listaPreOperatorio.add(diagnosticoDescricao);
		}
	}
	
	private Boolean validarDiagnosticoDescricao(List<MbcDiagnosticoDescricao> lista, MbcDiagnosticoDescricao diagnosticoDescricao){
		for(MbcDiagnosticoDescricao item: lista){
			if(item.getId().equals(diagnosticoDescricao.getId())){
				return true;
			}
		}
		return false;
	}
	
	public void inserirDiagnosticoDescricoesPosOperatorio( MbcDiagnosticoDescricao diagnosticoDescricao,
														   List<MbcDiagnosticoDescricao> listaPreOperatorio,
														   List<MbcDiagnosticoDescricao> listaPosOperatorio
														 ) throws ApplicationBusinessException {
		
		if(validarDiagnosticoDescricao(listaPosOperatorio,diagnosticoDescricao)){
			throw new ApplicationBusinessException(DiagnosticoDescricaoONExceptionCode.DIAGNOSTICO_DESCRICAO_OPERATORIO_DUPLICADO, diagnosticoDescricao.getCid().getCodigo());
			
		} else {
			final DiagnosticoDescricaoRN rn = getDiagnosticoDescricaoRN();
			rn.inserirDiagnosticoDescricoes(diagnosticoDescricao);
			listaPosOperatorio.add(diagnosticoDescricao);
		}
	}
	
	public void alterarDiagnosticoDescricao( MbcDiagnosticoDescricao diagnosticoDescricao) throws ApplicationBusinessException {
		final DiagnosticoDescricaoRN rn = getDiagnosticoDescricaoRN();
		rn.alterarDiagnosticoDescricoes(diagnosticoDescricao);
	}

	public void excluirDiagnosticoDescricoes(
			final MbcDiagnosticoDescricaoId id)
			throws ApplicationBusinessException, ApplicationBusinessException {
		
		final MbcDiagnosticoDescricaoDAO dao = getMbcDiagnosticoDescricaoDAO(); 
		MbcDiagnosticoDescricao dd = dao.obterPorChavePrimaria(id);
		
		getDiagnosticoDescricaoRN().excluirDiagnosticoDescricoes(dd);			
	}
	
	private MbcDiagnosticoDescricaoDAO getMbcDiagnosticoDescricaoDAO(){
		return mbcDiagnosticoDescricaoDAO;
	}
	
	protected DiagnosticoDescricaoRN getDiagnosticoDescricaoRN(){
		return diagnosticoDescricaoRN;
	}
}
