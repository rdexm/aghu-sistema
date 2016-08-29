package br.gov.mec.aghu.compras.pac.business;

import java.util.Comparator;

import br.gov.mec.aghu.compras.vo.RelatorioEspelhoPACVO;

class OrdenadorUnionRelatorioEspelhoPAC implements Comparator<RelatorioEspelhoPACVO> {

	@Override
	public int compare(RelatorioEspelhoPACVO arg0,
			RelatorioEspelhoPACVO arg1) {
		return arg0.getNumeroItemLicitacao().compareTo(arg1.getNumeroItemLicitacao());
	}
}
