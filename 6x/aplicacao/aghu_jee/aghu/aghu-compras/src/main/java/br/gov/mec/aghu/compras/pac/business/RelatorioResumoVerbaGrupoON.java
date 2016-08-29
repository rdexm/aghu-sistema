package br.gov.mec.aghu.compras.pac.business;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.vo.RelatorioResumoVerbaGrupoVO;
import br.gov.mec.aghu.compras.vo.VerbaGrupoSolicitacaoVO;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * @author danilosantos
 *
 */
@Stateless
public class RelatorioResumoVerbaGrupoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioResumoVerbaGrupoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoLicitacaoDAO scoLicitacaoDAO;

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8629570851651220582L;


	public RelatorioResumoVerbaGrupoVO obterRelatorioResumoVerbaGrupo(Integer numLicitacao){
		RelatorioResumoVerbaGrupoVO relatorioResumoVerbaGrupoVO = new RelatorioResumoVerbaGrupoVO(); 
		ScoLicitacao licitacao = this.getScoLicitacaoDAO().obterLicitacaoModalidadePorNumeroPac(numLicitacao);
		if(licitacao!=null){
			relatorioResumoVerbaGrupoVO.setNumero(licitacao.getNumero());
			relatorioResumoVerbaGrupoVO.setMlcCodigo(licitacao.getModalidadeLicitacao().getCodigo());
			relatorioResumoVerbaGrupoVO.setMlcDescricao(licitacao.getModalidadeLicitacao().getDescricao());
			relatorioResumoVerbaGrupoVO.setModalidadePac(licitacao.getModalidadeLicitacao().getCodigo()+"-"+licitacao.getModalidadeLicitacao().getDescricao());
			relatorioResumoVerbaGrupoVO.setDtDigitacao(licitacao.getDtDigitacao());
			if(licitacao.getModalidadeEmpenho() != null){
				relatorioResumoVerbaGrupoVO.setCodigoModalidadeEmpenho(licitacao.getModalidadeEmpenho().getCodigo());
				relatorioResumoVerbaGrupoVO.setDescricaoModalidadeEmpenho(licitacao.getModalidadeEmpenho().getDescricao());
				relatorioResumoVerbaGrupoVO.setModalidadeEmpenho(licitacao.getModalidadeEmpenho().getCodigo()+"-"+licitacao.getModalidadeEmpenho().getDescricao());
			}
			relatorioResumoVerbaGrupoVO.setListaCompras(this.getScoLicitacaoDAO().pesquisarDadosRelatorioResumoVerbasGrupoParaCompras(numLicitacao));
			relatorioResumoVerbaGrupoVO.setListaServicos(this.getScoLicitacaoDAO().pesquisarDadosRelatorioResumoVerbasGrupoParaServicos(numLicitacao));
			BigDecimal valorTotal = BigDecimal.valueOf(0);
			for(VerbaGrupoSolicitacaoVO compra: relatorioResumoVerbaGrupoVO.getListaCompras()){
				valorTotal = valorTotal.add(compra.getValorUnitario());
			}
			for(VerbaGrupoSolicitacaoVO servico: relatorioResumoVerbaGrupoVO.getListaServicos()){
				valorTotal = valorTotal.add(servico.getValorUnitario());
			}
			relatorioResumoVerbaGrupoVO.setValorTotal(valorTotal);
		}
		return relatorioResumoVerbaGrupoVO;
	}
	
	private ScoLicitacaoDAO getScoLicitacaoDAO(){
		return scoLicitacaoDAO;
	}
	

}
