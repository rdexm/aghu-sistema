package br.gov.mec.aghu.internacao.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.pesquisa.vo.DetalhesPesquisarSituacaoLeitosVO;
import br.gov.mec.aghu.internacao.vo.PesquisaSituacoesLeitosVO;
import br.gov.mec.aghu.internacao.vo.SituacaoLeitosVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SituacaoLeitosRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SituacaoLeitosRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinLeitosDAO ainLeitosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6091881894449773587L;
	private static final Comparator<DetalhesPesquisarSituacaoLeitosVO> COMPARATOR_DETALHES_SITUACAO_LEITO = new Comparator<DetalhesPesquisarSituacaoLeitosVO>() {

		@Override
		public int compare(DetalhesPesquisarSituacaoLeitosVO o1, DetalhesPesquisarSituacaoLeitosVO o2) {
			return o1.getSituacao().toUpperCase().compareTo(
					o2.getSituacao().toUpperCase());
		}

	};

	/**
	 * ORADB AINK_SIT_LEITOS.AINP_RET_SIT_LEITOS
	 * 
	 * Retorna as situações dos leitos de uma clinica passada como parâmetro.
	 * 
	 * @param clinica
	 * @return
	 */
	public PesquisaSituacoesLeitosVO pesquisaSituacoesLeitos(AghClinicas clinica) {
		PesquisaSituacoesLeitosVO pesquisaSituacoesLeitosVO = new PesquisaSituacoesLeitosVO();
		
		List<DetalhesPesquisarSituacaoLeitosVO> detalhes = new ArrayList<DetalhesPesquisarSituacaoLeitosVO>();
		Integer livre = 0;
		Integer ocupado = 0;
		Integer bloqueioLimpeza = 0;
		Integer bloqueio = 0;
		Integer bloqueioInfeccao = 0;
		Integer desativado = 0;
		Integer reserva = 0;
		
		List<SituacaoLeitosVO> situacoesLeitos = this
				.pesquisaSituacaoLeitos(clinica);
		if (situacoesLeitos != null && !situacoesLeitos.isEmpty()) {
			for (SituacaoLeitosVO situacaoLeito : situacoesLeitos) {
				if (situacaoLeito.getCodigoClinica()
						.equals(clinica.getCodigo())) {
					DominioMovimentoLeito grupoTipoMovimentoLeito = situacaoLeito
							.getGrupoTipoMovimentoLeito();
					if (DominioMovimentoLeito.L.equals(grupoTipoMovimentoLeito)) {
						livre += situacaoLeito.getQuantidade();
					} else if (DominioMovimentoLeito.O
							.equals(grupoTipoMovimentoLeito)) {
						ocupado += situacaoLeito.getQuantidade();
					} else if (DominioMovimentoLeito.BL
							.equals(grupoTipoMovimentoLeito)) {
						bloqueioLimpeza += situacaoLeito.getQuantidade();
					} else if (DominioMovimentoLeito.B
							.equals(grupoTipoMovimentoLeito)) {
						bloqueio += situacaoLeito.getQuantidade();
					} else if (DominioMovimentoLeito.BI
							.equals(grupoTipoMovimentoLeito)) {
						bloqueioInfeccao += situacaoLeito.getQuantidade();
					} else if (DominioMovimentoLeito.D
							.equals(grupoTipoMovimentoLeito)) {
						desativado += situacaoLeito.getQuantidade();
					} else if (DominioMovimentoLeito.R
							.equals(grupoTipoMovimentoLeito)) {
						reserva += situacaoLeito.getQuantidade();
					}
					
					detalhes.add(new DetalhesPesquisarSituacaoLeitosVO(
							situacaoLeito.getDescricaoTipoMovimentoLeito(),
							situacaoLeito.getQuantidade()));
				}
			}
		}

		Collections.sort(detalhes, COMPARATOR_DETALHES_SITUACAO_LEITO);
		
		pesquisaSituacoesLeitosVO.setDetalhes(detalhes);
		pesquisaSituacoesLeitosVO.setLivre(livre);
		pesquisaSituacoesLeitosVO.setOcupado(ocupado);
		pesquisaSituacoesLeitosVO.setBloqueioLimpeza(bloqueioLimpeza);
		pesquisaSituacoesLeitosVO.setBloqueio(bloqueio);
		pesquisaSituacoesLeitosVO.setBloqueioInfeccao(bloqueioInfeccao);
		pesquisaSituacoesLeitosVO.setDesativado(desativado);
		pesquisaSituacoesLeitosVO.setReserva(reserva);

		return pesquisaSituacoesLeitosVO;
	}

	/**
	 * ORADB V_AIN_SITUACAO_LEITOS
	 * 
	 * @param clinica
	 * @return
	 */
	public List<SituacaoLeitosVO> pesquisaSituacaoLeitos(AghClinicas clinica) {
		List<SituacaoLeitosVO> situacoesLeitos = new ArrayList<SituacaoLeitosVO>();

		// Não Ocupados
		List<Object[]> naoOcupados = this.getAinLeitosDAO().pesquisaLeitosDesocupados(clinica);

		Iterator<Object[]> ite = naoOcupados.iterator();
		while (ite.hasNext()) {
			Object[] nOcupado = ite.next();
			SituacaoLeitosVO situacaoLeito = new SituacaoLeitosVO();

			if (nOcupado[0] != null) {
				situacaoLeito.setCodigoClinica((Integer) nOcupado[0]);
			}
			if (nOcupado[1] != null) {
				situacaoLeito.setCodigoTipoMovimentoLeito((Short) nOcupado[1]);
			}
			if (nOcupado[2] != null) {
				situacaoLeito.setDescricaoTipoMovimentoLeito((String) nOcupado[2]);
			}
			if (nOcupado[3] != null) {
				situacaoLeito.setGrupoTipoMovimentoLeito((DominioMovimentoLeito) nOcupado[3]);
			}
			if (nOcupado[4] != null) {
				situacaoLeito.setQuantidade(((Long) nOcupado[4]).intValue());
			}

			situacoesLeitos.add(situacaoLeito);
		}

		return situacoesLeitos;
	}
	
	
	protected AinLeitosDAO getAinLeitosDAO(){
		return ainLeitosDAO;
	}

}
