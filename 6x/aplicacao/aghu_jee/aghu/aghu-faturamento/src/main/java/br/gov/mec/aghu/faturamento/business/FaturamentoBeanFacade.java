package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.ejb.ProcedHospInternoBeanLocal;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.ScoMaterial;

@Stateless
public class FaturamentoBeanFacade extends BaseFacade implements IFaturamentoBeanFacade {

	private static final long serialVersionUID = -235134586481027139L;
	
	@EJB
	private ProcedHospInternoBeanLocal procedHospInternoBeanLocal;
	
	@Override
	public void inserirProcedimentoHospitalarInterno(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, String descricao,
			DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		
		procedHospInternoBeanLocal.inserirProcedimentoHospitalarInterno(matCodigo, procedimentoCirurgico, 
				procedEspecialDiverso, csaCodigo, pheCodigo, descricao, indSituacao, euuSeq, cduSeq, cuiSeq, tidSeq);
	}
	
	@Override	
	public void atualizarProcedimentoHospitalarInternoSituacao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		
		procedHospInternoBeanLocal.atualizarProcedimentoHospitalarInternoSituacao(matCodigo, procedimentoCirurgico,
				procedEspecialDiverso, csaCodigo, pheCodigo, indSituacao, euuSeq, cduSeq, cuiSeq, tidSeq);
	}
	
	@Override
	public void atualizarProcedimentoHospitalarInternoDescricao(ScoMaterial matCodigo,
				MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
				String csaCodigo, String pheCodigo, String descricao, Short euuSeq, Integer cduSeq,
				Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		
		procedHospInternoBeanLocal.atualizarProcedimentoHospitalarInternoDescricao(matCodigo, procedimentoCirurgico,
				procedEspecialDiverso, csaCodigo, pheCodigo, descricao, euuSeq, cduSeq, cuiSeq, tidSeq);
	}
	
}
