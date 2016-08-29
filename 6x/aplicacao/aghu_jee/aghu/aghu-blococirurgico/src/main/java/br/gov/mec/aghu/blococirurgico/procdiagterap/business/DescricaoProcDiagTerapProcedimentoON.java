package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DescricaoProcDiagTerapProcedimentoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(DescricaoProcDiagTerapProcedimentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -531391226992771321L;
	
	protected enum DescricaoProcDiagTerapProcedimentoONExceptionCode implements BusinessExceptionCode {
		MBC_01384, MBC_01385, MBC_01386;
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: EVT_WHEN_LIST_CHANGED (form PDTF_DESCRICAO)
	 * 
	 * @param pciSeq
	 * @param novoIndContaminacao
	 * @throws ApplicationBusinessException
	 */
	public void validarContaminacaoProcedimentoCirurgicoPdt(Integer pciSeq, DominioIndContaminacao novoIndContaminacao) throws ApplicationBusinessException {
		
		// TODO: procedure migrada parcialmente pois lógica relacionada aos datablocks "DUAL_PID" e "DUAL" do forms será implementada futuramente.
		
		MbcProcedimentoCirurgicos procedimentoCirurgico = getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(pciSeq);
		DominioIndContaminacao indContaminacao = procedimentoCirurgico.getIndContaminacao();
		
		if (DominioIndContaminacao.P.equals(indContaminacao)
				&& DominioIndContaminacao.L.equals(novoIndContaminacao)) {
			/* Procedimento cadastrado com contaminação Procedimento Contaminada, 
			só pode ser alterado para Contaminada ou Infectada.*/
			throw new ApplicationBusinessException(DescricaoProcDiagTerapProcedimentoONExceptionCode.MBC_01384);

		} else if (DominioIndContaminacao.C.equals(indContaminacao)
				&& (DominioIndContaminacao.L.equals(novoIndContaminacao) || DominioIndContaminacao.P
						.equals(novoIndContaminacao))) {
			/* Procedimento cadastrado com contaminação  Contaminada,
		    só pode ser alterado para Infectado.*/			
			throw new ApplicationBusinessException(DescricaoProcDiagTerapProcedimentoONExceptionCode.MBC_01385);

		} else if (DominioIndContaminacao.I.equals(indContaminacao)
				&& !DominioIndContaminacao.I.equals(novoIndContaminacao)) {
			/* Procedimento cadastrado com contaminação Infectada, não pode ser alterado. */	
			throw new ApplicationBusinessException(DescricaoProcDiagTerapProcedimentoONExceptionCode.MBC_01386);
		}
	}
	
	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}	

}
