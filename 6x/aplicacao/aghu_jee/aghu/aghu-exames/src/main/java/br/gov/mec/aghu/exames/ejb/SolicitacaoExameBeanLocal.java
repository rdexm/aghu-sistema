package br.gov.mec.aghu.exames.ejb;

import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.dominio.DominioTipoTransporteUnidade;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
public interface SolicitacaoExameBeanLocal {
	
	void insert1() throws BaseException;
	
	void insert2() throws BaseException;
	
	SolicitacaoExameVO gravaSolicitacaoExame(SolicitacaoExameVO solicitacaoExameVO, String nomeMicrocomputador) throws BaseException;
	
	List<ItemContratualizacaoVO> gravaSolicitacaoExameContratualizacao(AelSolicitacaoExames aelSolicitacaoExames, List<ItemContratualizacaoVO> listaItensVO, String nomeMicrocomputador) throws BaseException;

	void receberItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador) throws BaseException;
	
	void voltarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador) throws BaseException;
	
	void atualizarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador)throws BaseException;
	
	public void atualizarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador)throws BaseException;
	
	void recepcionarPaciente(AelItemSolicitacaoExames itemSolicitacaoExames, final DominioTipoTransporteUnidade transporte, final Boolean indUsoO2Un, String nomeMicrocomputador)
			throws BaseException;

	AelSolicitacaoExames atualizarSolicitacaoExame(AelSolicitacaoExames solicExame,List<AelItemSolicitacaoExames> itemSolicExameExcluidos, String nomeMicrocomputador) throws BaseException;
	
}
