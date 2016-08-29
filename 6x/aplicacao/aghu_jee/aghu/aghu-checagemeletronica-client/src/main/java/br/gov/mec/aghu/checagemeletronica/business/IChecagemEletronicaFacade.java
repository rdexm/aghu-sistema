package br.gov.mec.aghu.checagemeletronica.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.model.EceJustificativaMdto;
import br.gov.mec.aghu.model.EceOcorrencia;
import br.gov.mec.aghu.model.EceOrdemDeAdministracao;
import br.gov.mec.aghu.model.EceOrdemDeAdministracaoHist;
import br.gov.mec.aghu.model.VMpmOcorrenciaPrcr;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Local
public interface IChecagemEletronicaFacade extends Serializable {

	boolean existeLocalizacao(final Integer newSeq, final Date truncaData, final Short newUnfSeq);

	void inserirOrdemLocalizacao(final Integer newSeq, final Date ontem, final Short newUnfSeq, final Short newQtoNum, final String newLeitoId) throws ApplicationBusinessException;

	void alterarOrdemLocalizacao(final Integer newSeq, final Date ontem, final Short newUnfSeq, final Short newQtoNum, final String newLeitoId) throws ApplicationBusinessException;

	void alterarOrdemLocalizacao(final EceOcorrencia eceOcorrencia);

	List<VMpmOcorrenciaPrcr> buscarMpmOcorrenciaPrcr(final Integer atdSeq, final Date dthrMovimento);

	EceOcorrencia obterPorChavePrimaria(final Integer ocoSeq);

	List<EceOrdemDeAdministracao> buscarOrdemAdmin(Integer atdSeq);

	List<EceOrdemDeAdministracaoHist>  buscarOrdemAdminHist(Integer atdSeq);

	List<EceJustificativaMdto> pesquisarJustificativasPorSeqDescricaoSituacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			Short seq, String descricao);

	Long pesquisarJustificativasPorSeqDescricaoSituacaoCount(Short seq, String descricao);

	EceJustificativaMdto obterEceJustificativaMdtoPorSeq(Short seqJustificativaMdto);

	void removerJustificativaMdto(Short seq) throws ApplicationBusinessException; 

	void persistirJustificativaMdto(EceJustificativaMdto justificativaMdto) throws ApplicationBusinessException;
	
}
