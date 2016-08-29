package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.Arrays;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.LoteDefaultVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.model.AghEspecialidades;

public class SelecionarRadioDefaultManager {
	public LoteDefaultVO getRadioDefault(IExamesFacade examesFacade, SolicitacaoExameVO solicitacaoExameVO) {
		SelecionarRadioDefault radioDefaultU = getSelecionarRadioDefaultUnidade(examesFacade, solicitacaoExameVO);
		SelecionarRadioDefault radioDefaultG = getSelecionarRadioDefaultGrupo(examesFacade);
		SelecionarRadioDefault radioDefaultE = getSelecionarRadioDefaultEspecialidade(examesFacade, solicitacaoExameVO.getAtendimento() == null ? null : solicitacaoExameVO.getAtendimento().getEspecialidade());
		
		DominioOrigemAtendimento[] dominiosGrupo = {DominioOrigemAtendimento.I, DominioOrigemAtendimento.N,
				DominioOrigemAtendimento.H, DominioOrigemAtendimento.X, DominioOrigemAtendimento.D, DominioOrigemAtendimento.C};
		
		DominioOrigemAtendimento[] dominiosEspecialidade = {DominioOrigemAtendimento.A, DominioOrigemAtendimento.U};

		if (Arrays.asList(dominiosGrupo).contains(solicitacaoExameVO.getOrigem())) {
			radioDefaultU.setProximo(radioDefaultG);
		} else if(Arrays.asList(dominiosEspecialidade).contains(solicitacaoExameVO.getOrigem())) {
			radioDefaultU.setProximo(radioDefaultE);
		}
		
		return radioDefaultU.executar();
	}
	
	protected SelecionarRadioDefault getSelecionarRadioDefaultUnidade(IExamesFacade examesFacade, SolicitacaoExameVO solicitacaoExameVO) {
		return new SelecionarRadioDefaultUnidade(examesFacade, solicitacaoExameVO);
	}
	
	protected SelecionarRadioDefault getSelecionarRadioDefaultGrupo(IExamesFacade examesFacade) {
		return new SelecionarRadioDefaultGrupo(examesFacade);
	}
	
	protected SelecionarRadioDefault getSelecionarRadioDefaultEspecialidade(IExamesFacade examesFacade, AghEspecialidades especialidade) {
		return new SelecionarRadioDefaultEspecialidade(examesFacade, especialidade);
	}
}
