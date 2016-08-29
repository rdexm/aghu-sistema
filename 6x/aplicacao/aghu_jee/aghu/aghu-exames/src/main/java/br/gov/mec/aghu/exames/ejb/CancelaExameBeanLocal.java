package br.gov.mec.aghu.exames.ejb;

import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameCancelamentoVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface CancelaExameBeanLocal {

	void excluirItensExamesSelecionados(List<ItemSolicitacaoExameCancelamentoVO> itens, String nomeMicrocomputador) throws BaseException;

	void cancelarExames(AelItemSolicitacaoExames aelItemSolicitacaoExames, final AelMotivoCancelaExames motivoCancelar, String nomeMicrocomputador) throws BaseException;

	void estornarItemSolicitacaoExame(AelItemSolicitacaoExames item, String nomeMicrocomputador) throws BaseException;

}
