package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamCapacidadeAtendDAO;
import br.gov.mec.aghu.emergencia.vo.MamCapacidadeAtendVO;
import br.gov.mec.aghu.model.MamCapacidadeAtend;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.ServiceException;
/**
 * @author israel.haas
 */
@Stateless
public class CapacidadeAtendimentoON extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@Inject
	private MamCapacidadeAtendDAO mamCapacidadeAtendDAO;
	
	@Inject IConfiguracaoService configuracaoService;
	
	@EJB
	private CapacidadeAtendimentoRN capacidadeAtendimentoRN;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum CapacidadeAtendimentoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL, ERRO_QTD_INICIAL_FINAL_PAC_MAIOR_ZERO,
		ERRO_QTD_INICIAL_MENOR_IGUAL_FINAL, ERRO_CAP_ATEND_MAIOR_ZERO, ERRO_COLISAO_QUANTIDADES_PACIENTES
	}
	
	public List<MamCapacidadeAtendVO> pesquisarCapacidadesAtends(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short espSeq, DominioSituacao indSituacao) throws ApplicationBusinessException {
		List<MamCapacidadeAtend> listaCapacidadeAtend = this.mamCapacidadeAtendDAO
				.pesquisarCapacidadesAtends(firstResult, maxResults, orderProperty, asc, espSeq, indSituacao);
		
		List<MamCapacidadeAtendVO> listaRetorno = new ArrayList<MamCapacidadeAtendVO>();
		for (MamCapacidadeAtend item : listaCapacidadeAtend) {
			MamCapacidadeAtendVO vo = new MamCapacidadeAtendVO();
			vo.setSeq(item.getSeq());
			vo.setQtdeInicialPac(item.getQtdeInicialPac());
			vo.setQtdeFinalPac(item.getQtdeFinalPac());
			vo.setCapacidadeAtend(item.getCapacidadeAtend());
			vo.setIndSituacao(item.getIndSituacao());
			vo.setNomeEspecialidade(pesquisarNomeEspecialidadePorSeq(item.getMamEmgEspecialidades().getEspSeq()));
			vo.setEspSeq(item.getMamEmgEspecialidades().getEspSeq());
			
			listaRetorno.add(vo);
		}
		CoreUtil.ordenarLista(listaRetorno, MamCapacidadeAtendVO.Fields.QTDE_INICIAL_PAC.toString(), true);
		CoreUtil.ordenarLista(listaRetorno, MamCapacidadeAtendVO.Fields.NOME_ESPECIALIDADE.toString(), true);
		return listaRetorno;
	}
	
	public Long pesquisarCapacidadesAtendsCount(Short espSeq, DominioSituacao indSituacao) {
		return this.mamCapacidadeAtendDAO.pesquisarCapacidadesAtendsCount(espSeq, indSituacao);
	}
	
	public String pesquisarNomeEspecialidadePorSeq(Short seq) throws ApplicationBusinessException {
		String result = null;
		try {
			result = this.configuracaoService.pesquisarNomeEspecialidadePorSeq(seq);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(CapacidadeAtendimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	public void persistirCapacidadeAtend(Short espSeq, Integer capacidadeSeq, Short qtdeInicialPac, Short qtdeFinalPac,
			Short capacidadeAtend, Boolean indSituacao) throws ApplicationBusinessException {
		
		if (qtdeInicialPac <= 0 || qtdeFinalPac <= 0) {
			throw new ApplicationBusinessException(CapacidadeAtendimentoONExceptionCode.ERRO_QTD_INICIAL_FINAL_PAC_MAIOR_ZERO);
		}
		if (qtdeInicialPac.compareTo(qtdeFinalPac) > 0) {
			throw new ApplicationBusinessException(CapacidadeAtendimentoONExceptionCode.ERRO_QTD_INICIAL_MENOR_IGUAL_FINAL);
		}
		if (capacidadeAtend < 0) {
			throw new ApplicationBusinessException(CapacidadeAtendimentoONExceptionCode.ERRO_CAP_ATEND_MAIOR_ZERO);
		}
		if (this.mamCapacidadeAtendDAO.verificaExisteColisaoQtdPacientes(espSeq, capacidadeSeq, qtdeInicialPac, qtdeFinalPac)) {
			throw new ApplicationBusinessException(CapacidadeAtendimentoONExceptionCode.ERRO_COLISAO_QUANTIDADES_PACIENTES);
		}
		
		if (capacidadeSeq != null) {
			this.capacidadeAtendimentoRN.atualizarCapacidadeAtend(capacidadeSeq, qtdeInicialPac, qtdeFinalPac, capacidadeAtend, indSituacao);
			
		} else {
			this.capacidadeAtendimentoRN.inserirCapacidadeAtend(espSeq, qtdeInicialPac, qtdeFinalPac, capacidadeAtend, indSituacao);
		}
	}
}
