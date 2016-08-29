package br.gov.mec.aghu.faturamento.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Local
public interface ProcedHospInternoBeanLocal {
	
	void inserirProcedimentoHospitalarInterno(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, String descricao,
			DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException;
	
	void atualizarProcedimentoHospitalarInternoSituacao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException;
	
	void atualizarProcedimentoHospitalarInternoDescricao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, String descricao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException;	

}
