package br.gov.mec.aghu.blococirurgico.agendamento.business;


import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoPadraoDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.model.MbcDescricaoPadrao;
import br.gov.mec.aghu.model.MbcDescricaoPadraoId;

@Modulo(ModuloEnum.BLOCO_CIRURGICO)
@Stateless
public class BlocoCirurgicoAgendamentoFacade extends BaseFacade implements IBlocoCirurgicoAgendamentoFacade{

	@Inject
	private MbcDescricaoPadraoDAO mbcDescricaoPadraoDAO;


	@EJB
	private MbcDescricaoPadraoON mbcDescricaoPadraoON;

	/**
	 * 
	 */
	private static final long serialVersionUID = 852973077243931569L;

	@Override
	@Secure("#{s:hasPermission('manterDescricaoTecnicaPadrao', 'pesquisar')}")
	public List<MbcDescricaoPadrao> buscarDescricaoPadrao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Short especialidadeId,Integer procedimentoId, String titulo) {
		return getMbcDescricaoDAO().buscarMbcDescricaoPadrao(firstResult, maxResult, orderProperty, asc, especialidadeId, procedimentoId, titulo);
	}

	@Override
	public Long contarDescricaoPadrao(Short especialidadeId, Integer procedimentoId, String titulo) {
		return getMbcDescricaoDAO().contarMbcDescricaoPadrao(especialidadeId, procedimentoId, titulo);
	}

	@Override
	public MbcDescricaoPadrao obterDescricaoPadraoById(Integer seqp, Short espSeq) {
		return getMbcDescricaoDAO().obterDescricaoPadraoById(seqp, espSeq);
	}

	@Override
	@Secure("#{s:hasPermission('manterDescricaoTecnicaPadrao', 'persistir')}")
	public void persistirMbcDescricaopadrao(MbcDescricaoPadrao descricaoPadrao) throws BaseException {
		this.getMbcDescricaoON().persistirDescricaoPadrao(descricaoPadrao);
		
	}

	@Override
	@Secure("#{s:hasPermission('manterDescricaoTecnicaPadrao', 'excluir')}")
	public void excluirDescricaoPadrao(MbcDescricaoPadraoId id) throws BaseException {
		this.getMbcDescricaoON().remover(id);
		
	}
	
	public MbcDescricaoPadrao obterPorChavePrimaria(MbcDescricaoPadraoId id) throws BaseException {
		return this.getMbcDescricaoON().obterPorChavePrimaria(id);
	}

	
	private MbcDescricaoPadraoDAO getMbcDescricaoDAO() {
		return mbcDescricaoPadraoDAO;
	}
	
	private MbcDescricaoPadraoON getMbcDescricaoON() {
		return mbcDescricaoPadraoON;
	}	
}