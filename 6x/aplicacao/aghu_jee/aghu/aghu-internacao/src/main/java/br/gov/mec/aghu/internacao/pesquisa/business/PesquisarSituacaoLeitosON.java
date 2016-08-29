package br.gov.mec.aghu.internacao.pesquisa.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.vo.DetalhesPesquisarSituacaoLeitosVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisarSituacaoLeitosVO;
import br.gov.mec.aghu.internacao.vo.PesquisaSituacoesLeitosVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;

@Stateless
public class PesquisarSituacaoLeitosON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisarSituacaoLeitosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IInternacaoFacade internacaoFacade;

@EJB
private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6793634914346143904L;

	public Long pesquisaSituacaoLeitosCount(AghClinicas clinica) {
		if (clinica == null) {
			List<AghClinicas> clinicas = getAghuFacade().pesquisaSituacaoLeitos(null);
			Long count = (long) clinicas.size();
			return count == 0 ? 0 : count + 2; // O retorno será a soma do total
												// de clínicas mais o
												// totalizador da tela e unidades funcionais.
		} else {
			return (long) 3; // O retorno será 3, referente a própria clínica passada
						// por parâmetro mais o totalizador e unidades funcionais.
		}
	}

	@Secure("#{s:hasPermission('leito','pesquisar')}")
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<PesquisarSituacaoLeitosVO> pesquisaSituacaoLeitos(
			AghClinicas clinicaParam, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		
		List<AghClinicas> clinicas = getAghuFacade().pesquisaSituacaoLeitos(clinicaParam, orderProperty, asc);
		List<PesquisarSituacaoLeitosVO> situacoesLeitosVO = new ArrayList<PesquisarSituacaoLeitosVO>();
		
		if (clinicas != null) {
			Integer totalCapacidadeInstalada = 0;
			Integer totalCapacidadeOperacao = 0;
			Integer totalLivre = 0;
			Integer totalOcupado = 0;
			Integer totalBloqueioLimpeza = 0;
			Integer totalBloqueioTotal = 0;
			Integer totalDesativado = 0;
			Integer totalReserva = 0;
			PesquisarSituacaoLeitosVO situacaoLeitoVO;
			
			for(AghClinicas clinica : clinicas) {
				situacaoLeitoVO = new PesquisarSituacaoLeitosVO();
				situacaoLeitoVO.setDescricao(clinica.getDescricao());
				
				PesquisaSituacoesLeitosVO pesquisaSituacoesLeitosVO = this.getInternacaoFacade().pesquisaSituacoesLeitos(clinica);
				
				situacaoLeitoVO.setDescricao(clinica.getDescricao());
				situacaoLeitoVO.setCapacidadeInstalada(this.getInternacaoFacade().pesquisaCapacidadeInstaladaLeitos(clinica).getQuantidade());
				situacaoLeitoVO.setDetalhes(pesquisaSituacoesLeitosVO.getDetalhes());
				situacaoLeitoVO
						.setLivre(pesquisaSituacoesLeitosVO.getLivre() != null ? pesquisaSituacoesLeitosVO
								.getLivre()
								: 0);
				
				situacaoLeitoVO.setOcupado(pesquisaSituacoesLeitosVO
						.getOcupado() != null ? pesquisaSituacoesLeitosVO
						.getOcupado() : 0);

				situacaoLeitoVO.setBloqueioLimpeza(pesquisaSituacoesLeitosVO
						.getBloqueioLimpeza() != null ? pesquisaSituacoesLeitosVO
								.getBloqueioLimpeza() : 0);

				situacaoLeitoVO.setBloqueio(pesquisaSituacoesLeitosVO
						.getBloqueio() != null ? pesquisaSituacoesLeitosVO
								.getBloqueio() : 0);

				situacaoLeitoVO.setBloqueioInfeccao(pesquisaSituacoesLeitosVO
						.getBloqueioInfeccao() != null ? pesquisaSituacoesLeitosVO
								.getBloqueioInfeccao() : 0);
				
				situacaoLeitoVO.setBloqueioTotal(pesquisaSituacoesLeitosVO
						.getBloqueio()  +  pesquisaSituacoesLeitosVO
								.getBloqueioInfeccao() );
				
				situacaoLeitoVO.setDesativado(pesquisaSituacoesLeitosVO
						.getDesativado() != null ? pesquisaSituacoesLeitosVO
								.getDesativado() : 0);
				
				situacaoLeitoVO.setReserva(pesquisaSituacoesLeitosVO
						.getReserva() != null ? pesquisaSituacoesLeitosVO
								.getReserva() : 0);

				situacaoLeitoVO.setCapacidadeOperacao(situacaoLeitoVO.getCapacidadeInstalada().intValue() - (situacaoLeitoVO.getBloqueioTotal()));
				
				// Totalizadores
				totalCapacidadeInstalada += situacaoLeitoVO.getCapacidadeInstalada();
				totalCapacidadeOperacao += situacaoLeitoVO.getCapacidadeOperacao();
				totalLivre += situacaoLeitoVO.getLivre();
				totalOcupado += situacaoLeitoVO.getOcupado();
				totalBloqueioLimpeza += situacaoLeitoVO.getBloqueioLimpeza();
				totalBloqueioTotal += situacaoLeitoVO.getBloqueioTotal();
				totalDesativado += situacaoLeitoVO.getDesativado();
				totalReserva += situacaoLeitoVO.getReserva();
				
				situacoesLeitosVO.add(situacaoLeitoVO);
			}
			
			// inicio 
			
			situacaoLeitoVO = new PesquisarSituacaoLeitosVO();
//			situacaoLeitoVO.setDescricao(super.resourceBundle.getString("LABEL_SEM_LEITO"));
			situacaoLeitoVO.setDescricao("EM UNIDADES FUNCIONAIS");
			situacaoLeitoVO.setCapacidadeInstalada(0);
			situacaoLeitoVO.setDetalhes(new ArrayList<DetalhesPesquisarSituacaoLeitosVO>());
			situacaoLeitoVO.setLivre(0);			
			situacaoLeitoVO.setOcupado(this.getInternacaoFacade().pesquisaSituacaoSemLeitos().getQuantidade());
			situacaoLeitoVO.setBloqueioLimpeza(0);
			situacaoLeitoVO.setBloqueio(0);
			situacaoLeitoVO.setBloqueioInfeccao(0);			
			situacaoLeitoVO.setBloqueioTotal(0);
			situacaoLeitoVO.setDesativado(0);			
			situacaoLeitoVO.setReserva(0);			
			situacaoLeitoVO.setCapacidadeOperacao(0);	
			situacoesLeitosVO.add(situacaoLeitoVO);
			totalOcupado += situacaoLeitoVO.getOcupado();
			
			// fim
			
			
			// Adiciona o totalizador
			PesquisarSituacaoLeitosVO totalizador = new PesquisarSituacaoLeitosVO();
			totalizador.setDescricao("TOTAL");
			totalizador.setCapacidadeInstalada(totalCapacidadeInstalada);
			totalizador.setCapacidadeOperacao(totalCapacidadeOperacao);
			totalizador.setLivre(totalLivre);
			totalizador.setOcupado(totalOcupado);
			totalizador.setBloqueioLimpeza(totalBloqueioLimpeza);
			totalizador.setBloqueioTotal(totalBloqueioTotal);
			totalizador.setDesativado(totalDesativado);
			totalizador.setReserva(totalReserva);

			// Cálculo do percentual de leitos livres total
			BigDecimal totalPercentualOcupacao = BigDecimal.ZERO;
			if (totalizador.getCapacidadeOperacao() != 0) {
				totalPercentualOcupacao = new BigDecimal(totalizador.getOcupado());
				totalPercentualOcupacao = totalPercentualOcupacao
						.divide(new BigDecimal(totalizador
								.getCapacidadeOperacao() != null ? totalizador
								.getCapacidadeOperacao() : 1), 4, BigDecimal.ROUND_HALF_EVEN);
				totalPercentualOcupacao = totalPercentualOcupacao
						.multiply(new BigDecimal(100));
			}
			totalizador.setPercentualOcupacao(totalPercentualOcupacao);

			situacoesLeitosVO.add(totalizador);
		}
		
		if(firstResult != null && maxResult != null) {
			// A paginação é feita de forma manual, eliminando elementos que não
			// devem aparecer no resultado da pesquisa.
			if (situacoesLeitosVO.size() > maxResult && firstResult < situacoesLeitosVO.size()) {
				int toIndex = firstResult + maxResult;
	
				if (toIndex > situacoesLeitosVO.size()) {
					toIndex = situacoesLeitosVO.size();
				}
	
				situacoesLeitosVO = situacoesLeitosVO.subList(firstResult, toIndex);
			}
		}
		
		return situacoesLeitosVO;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

}
