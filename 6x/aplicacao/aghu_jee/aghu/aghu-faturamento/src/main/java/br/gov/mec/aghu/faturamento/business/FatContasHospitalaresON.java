package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOrtesesProtesesVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FatContasHospitalaresON extends BaseBusiness {

	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;

	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 6911714298333698798L;

	@Override
	protected Log getLogger() {
		return LogFactory.getLog(FatContasHospitalaresON.class);
	}

	/**
	 * @ORADB FATC_BUSCA_CUM
	 * @param procedimento
	 * @param ano
	 * @param mes
	 * @param dtHrInicio
	 * @param iniciaisPaciente
	 * @param dtIni
	 * @param dtFim
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<RelacaoDeOrtesesProtesesVO> obterRelacaoDeOrtesesProteses(final Long procedimento, final Integer ano, final Integer mes, final Date dtHrInicio, final String iniciaisPaciente,final Date dtIni,final Date dtFim) throws ApplicationBusinessException{
		List<RelacaoDeOrtesesProtesesVO> listaOrtesesProteses = fatEspelhoAihDAO.obterRelacaoDeOrtesesProteses(
				procedimento, ano, mes, dtHrInicio, iniciaisPaciente, dtIni, dtFim); 
		List<RelacaoDeOrtesesProtesesVO> listaOrtesesProtesesGroupBy = new ArrayList<RelacaoDeOrtesesProtesesVO>();
		if (listaOrtesesProteses != null && !listaOrtesesProteses.isEmpty()) {
			for (RelacaoDeOrtesesProtesesVO relacaoDeOrtesesProtesesVO : listaOrtesesProteses) {
				//FATC_BUSCA_CUM
				RelacaoDeOrtesesProtesesVO relacaoDeOrtesesProtesesVOaux = new RelacaoDeOrtesesProtesesVO();
				relacaoDeOrtesesProtesesVOaux = scoFornecedorDAO.buscarDadosFornecedoresICH(relacaoDeOrtesesProtesesVO);
				if (relacaoDeOrtesesProtesesVOaux != null) {
					relacaoDeOrtesesProtesesVO.setDatautlDate(relacaoDeOrtesesProtesesVOaux.getDatautlDate());
					relacaoDeOrtesesProtesesVO.setSrmseq(relacaoDeOrtesesProtesesVOaux.getSrmseq());
					relacaoDeOrtesesProtesesVO.setCgcfornecedor(relacaoDeOrtesesProtesesVOaux.getCgcfornecedor());
				}
				//GROUP BY
				if (!listaOrtesesProtesesGroupBy.contains(relacaoDeOrtesesProtesesVO)) {
					listaOrtesesProtesesGroupBy.add(relacaoDeOrtesesProtesesVO);
				}
			}
			//ORDER BY
			Collections.sort(listaOrtesesProtesesGroupBy, new FatContasHospitalaresComparator());
			formatarRelatorio(listaOrtesesProtesesGroupBy);
		}

		return listaOrtesesProtesesGroupBy;
	}

	private void formatarRelatorio(List<RelacaoDeOrtesesProtesesVO> listaOrtesesProtesesGroupBy) {
		RelacaoDeOrtesesProtesesVO relacaoDeOrtesesProtesesVOAnterior = null;
		for (RelacaoDeOrtesesProtesesVO relacaoDeOrtesesProtesesVO : listaOrtesesProtesesGroupBy) {
			if (relacaoDeOrtesesProtesesVOAnterior != null) {
				relacaoDeOrtesesProtesesVO.setPacNomeLinhaAnterior(relacaoDeOrtesesProtesesVOAnterior.getPacnome());
				String srmSeq = relacaoDeOrtesesProtesesVOAnterior.getSrmseq() == null ? "" : relacaoDeOrtesesProtesesVOAnterior.getSrmseq().toString();
				String cgc = relacaoDeOrtesesProtesesVOAnterior.getCgcfornecedor() == null ? "" : relacaoDeOrtesesProtesesVOAnterior.getCgcfornecedor().toString();
				relacaoDeOrtesesProtesesVO.setFatBuscaCumLinhaAnterior(srmSeq + relacaoDeOrtesesProtesesVOAnterior.getDatautl()+cgc);
			}
			relacaoDeOrtesesProtesesVOAnterior = relacaoDeOrtesesProtesesVO;
		}
	}
}

class FatContasHospitalaresComparator implements Comparator<RelacaoDeOrtesesProtesesVO> {
	@Override
	public int compare(RelacaoDeOrtesesProtesesVO o1, RelacaoDeOrtesesProtesesVO o2) {
		if (obterValor(o1.getPacnome()).compareTo(obterValor(o2.getPacnome())) == 0) {
			if (obterValor(o1.getSrmseq()).compareTo(obterValor(o2.getSrmseq())) == 0) {
				if (obterValor(o1.getQtde()).compareTo(obterValor(o2.getQtde())) == 0) {
					return compareValorApres(o1.getValorapres(), o2.getValorapres());
				}
				return obterValor(o1.getQtde()).compareTo(obterValor(o2.getQtde()));
			}
			return obterValor(o1.getSrmseq()).compareTo(obterValor(o2.getSrmseq()));
		}
		return obterValor(o1.getPacnome()).compareTo(obterValor(o2.getPacnome()));
	}
	
	/**
	 * * Se o valor for nulo, retorna um Long 0.
	 * @param valor
	 * @return
	 */
	private Long obterValor(Long valor) {
		if (valor == null) {
			return Long.valueOf(0);
		}
		else {
			return valor;
		}
	}
	
	/**
	 * Se o valor for nulo, retorna um Inteiro 0.
	 * @param valor
	 * @return
	 */
	private Integer obterValor(Integer valor) {
		if (valor == null) {
			return 0;
		}
		else {
			return valor;
		}
	}
	
	/**
	 * * Se o valor for nulo, retorna um String "".
	 * @param valor
	 * @return
	 */
	private String obterValor(String valor) {
		if (valor == null) {
			return "";
		}
		else {
			return valor;
		}
	}

	private int compareValorApres(BigDecimal valorapres, BigDecimal valorapres2) {
		BigDecimal valorApres1 = valorapres == null ? BigDecimal.ZERO : valorapres;
		BigDecimal valorApres2 = valorapres2 == null ? BigDecimal.ZERO : valorapres2;
		return valorApres1.compareTo(valorApres2);
	}
}