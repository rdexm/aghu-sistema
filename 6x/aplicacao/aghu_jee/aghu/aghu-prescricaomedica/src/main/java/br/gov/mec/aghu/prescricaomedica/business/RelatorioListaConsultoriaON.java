package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndConcluidaSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioSituacaoConsultoria;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultoriasInternacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.StringUtil;

@Stateless
public class RelatorioListaConsultoriaON extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(RelatorioListaConsultoriaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private static final long serialVersionUID = 7245912448693429256L;
	
	/**
	 * Método utilizado para popular/formatar os dados que serão exibidos no
	 *  relatório Lista de Solicitações de Consultoria
	 * 
	 * @param colecao
	 * @return
	 */
	public List<ConsultoriasInternacaoVO> formatarColecaoRelatorioConsultorias(List<ConsultoriasInternacaoVO> colecao,
			DominioSituacaoConsultoria situacaoFiltro) {
		
		for (ConsultoriasInternacaoVO consultoriasInternacaoVO : colecao) {
			
			AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(consultoriasInternacaoVO.getAtdSeq());
			// Leito
			consultoriasInternacaoVO.setLeitoRelatorio(StringUtil.trunc(consultoriasInternacaoVO.getLocalPac(), false, Long.valueOf("8")));
			// Nome Paciente
			consultoriasInternacaoVO.setNomePacRelatorio(StringUtil.trunc(consultoriasInternacaoVO.getNome(), false, Long.valueOf("35")));
			// Sigla da especialidade
			consultoriasInternacaoVO.setEspSigla(atendimento.getEspecialidade().getSigla());			
			// Nome da equipe
			if(atendimento.getServidor() != null && atendimento.getServidor().getPessoaFisica() != null) {
				if (atendimento.getServidor().getPessoaFisica().getNomeUsual() != null) {
					consultoriasInternacaoVO.setEquipe(atendimento.getServidor().getPessoaFisica().getNomeUsual());
				} else {
					consultoriasInternacaoVO.setEquipe(StringUtil.trunc(atendimento.getServidor().getPessoaFisica().getNome(),
							false, Long.valueOf("15")));
				}
			}
			// Situação
			if (DominioIndConcluidaSolicitacaoConsultoria.S.equals(consultoriasInternacaoVO.getIndConcluida())) {
				consultoriasInternacaoVO.setDrvSituacao("Aval");
			} else if (DominioIndConcluidaSolicitacaoConsultoria.A.equals(consultoriasInternacaoVO.getIndConcluida())) {
				consultoriasInternacaoVO.setDrvSituacao("Acomp");
			} else if (DominioIndConcluidaSolicitacaoConsultoria.N.equals(consultoriasInternacaoVO.getIndConcluida())) {
				consultoriasInternacaoVO.setDrvSituacao("Pend");
			}
		}
		
		// ORDENAÇÃO		
		if (situacaoFiltro != null && (situacaoFiltro.equals(DominioSituacaoConsultoria.CO) || situacaoFiltro.equals(DominioSituacaoConsultoria.A))) {
			Collections.sort(colecao, Collections.reverseOrder(new Comparator<ConsultoriasInternacaoVO>() {
				@Override
				public int compare(ConsultoriasInternacaoVO o1, ConsultoriasInternacaoVO o2) {
					return o1.getLocalPac().compareTo(o2.getLocalPac());
				}
			}));
			
		} else if (situacaoFiltro != null && situacaoFiltro.equals(DominioSituacaoConsultoria.P)) {
			Collections.sort(colecao, new Comparator<ConsultoriasInternacaoVO>() {
				@Override
				public int compare(ConsultoriasInternacaoVO o1, ConsultoriasInternacaoVO o2) {
					return o1.getCriadoEm().compareTo(o2.getCriadoEm());
				}
			});
		} else {
			Collections.sort(colecao, new Comparator<ConsultoriasInternacaoVO>() {
				@Override
				public int compare(ConsultoriasInternacaoVO o1, ConsultoriasInternacaoVO o2) {
					return o1.getLocalPac().compareTo(o2.getLocalPac());
				}
			});
		}
		
		return colecao;		
	}
}
