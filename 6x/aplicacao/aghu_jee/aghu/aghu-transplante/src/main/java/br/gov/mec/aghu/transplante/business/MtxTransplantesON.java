package br.gov.mec.aghu.transplante.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.MtxResultadoExames;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.transplante.dao.MtxResultadoExamesDAO;
import br.gov.mec.aghu.transplante.dao.MtxTransplantesDAO;
import br.gov.mec.aghu.transplante.vo.ListarTransplantesVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class MtxTransplantesON extends BaseBusiness {
	
	@Inject
	private MtxResultadoExamesDAO mtxResultadoExamesDAO;
	
	@Inject
	private MtxTransplantesDAO mtxTransplantesDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4503549137483826912L;

	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private enum RelatorioTransplanteOrgaoTMOSituacaoONExceptionCode implements BusinessExceptionCode {
		NENHUM_REGISTRO_ENCONTRADO;
	}
	
	private enum MtxTransplantesONExceptionCode implements BusinessExceptionCode {
		CAMPO_TIPO_TMO_OBRIGATORIO;
	}
	
	public void validarTipoTransplante(ListarTransplantesVO filtro) throws ApplicationBusinessException {
		if(filtro.getTransplanteTipoTmo() == null){
			throw new ApplicationBusinessException(MtxTransplantesONExceptionCode.CAMPO_TIPO_TMO_OBRIGATORIO, Severity.ERROR);
		}
	}
	
	/**
	 * #47146
	 * Remove resultado exames
	 */
	public void excluirResultadoExames(MtxResultadoExames resultadoExames){
		
		if(resultadoExames!=null){	
			
			mtxResultadoExamesDAO.removerPorId(resultadoExames.getSeq());
		}
	}
	
	/**
	 * #47146
	 * Inserir resultado exames 
	 */
		public void salvarResultadoExames(MtxResultadoExames resultadoExames, Integer seqTransplante) {
			
			if (resultadoExames != null) {
				
			MtxTransplantes transplante =mtxTransplantesDAO.obterPorChavePrimaria(seqTransplante);
			resultadoExames.setTransplante(transplante);
				mtxResultadoExamesDAO.persistir(resultadoExames);
				}
		}
		
		/**
		 * #47146
		 * Atualizar resultado exames 
		 */
		
		public void atualizarResultadoExames(MtxResultadoExames resultadoExames){
			
			if (resultadoExames != null) {
				
				MtxResultadoExames mtxResultadoExamesAux = mtxResultadoExamesDAO.obterPorChavePrimaria(resultadoExames.getSeq());
				mtxResultadoExamesAux.setResultado(resultadoExames.getResultado());
				mtxResultadoExamesAux.setData(resultadoExames.getData());
				mtxResultadoExamesDAO.merge(mtxResultadoExamesAux);
				}
		}	
}
