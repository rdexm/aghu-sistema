package br.gov.mec.aghu.certificacaodigital.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.certificacaodigital.dao.AghVersaoDocumentoDAO;
import br.gov.mec.aghu.certificacaodigital.vo.DocumentosPendentesVO;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ListarPendenciasAssinaturaON extends BaseBusiness {


private static final Log LOG = LogFactory.getLog(ListarPendenciasAssinaturaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AghVersaoDocumentoDAO aghVersaoDocumentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8173850417351381456L;

	/**
	 * @author gandriotti
	 * @param responsavel
	 * @return
	 */
	public DocumentosPendentesVO pesquisarPendenciaMaisAntiga(
			final RapServidores responsavel) {
		
		DocumentosPendentesVO result = null;
		AghVersaoDocumento parcial = null;
		AghVersaoDocumentoDAO dao = null;

		dao = this.getAghVersaoDocumentoDAO();
		parcial = dao.pesquisarPendenciaMaisAntiga(responsavel);
		if (parcial!=null && parcial.getAghDocumentos()!=null) {
			result = this.popularDocumentoPendente(parcial);
		}
		
		return result;
	}
	
	
	public List<DocumentosPendentesVO> pesquisarPendentesResponsavel(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final RapServidores responsavel) {

		List<AghVersaoDocumento> lista = this.getAghVersaoDocumentoDAO()
				.pesquisarPendentesResponsavel(firstResult, maxResult,
						orderProperty, asc, responsavel);

		List<DocumentosPendentesVO> result = popularListaDocumentosPendentes(lista);

		return result;
	}

	//Tipo 1
	public List<DocumentosPendentesVO> pesquisarPendentesResponsavelPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores responsavel, AipPacientes paciente) {

		List<AghVersaoDocumento> lista = this.getAghVersaoDocumentoDAO()
				.pesquisarPendentesResponsavelPaciente(firstResult, maxResult,
						orderProperty, asc, responsavel, paciente);

		List<DocumentosPendentesVO> result = popularListaDocumentosPendentes(lista);

		return result;
	}
	
	//Tipo 2
		public List<DocumentosPendentesVO> pesquisarPendentes(Integer firstResult,
				Integer maxResult, String orderProperty, boolean asc,
				RapServidores responsavel, AipPacientes paciente) {

			List<AghVersaoDocumento> lista = this.getAghVersaoDocumentoDAO()
					.pesquisarPendentes(firstResult, maxResult, orderProperty, asc,
							responsavel, paciente);

			List<DocumentosPendentesVO> result = popularListaDocumentosPendentes(lista);

			return result;
		}

	//Tipo 3
	public List<DocumentosPendentesVO> pesquisarPendentesPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores responsavel, AipPacientes paciente) {

		List<AghVersaoDocumento> lista = this.getAghVersaoDocumentoDAO()
				.pesquisarPendentesPaciente(firstResult, maxResult,
						orderProperty, asc, responsavel, paciente);

		List<DocumentosPendentesVO> result = popularListaDocumentosPendentes(lista);

		return result;
	}
	
	/**
	 * Pesquisa os documentos pendentes e os documentos do responsável em
	 * uma unica lista, utilizado na Visão: Meus Documentos na lista de pacientes
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param responsavel
	 * @param paciente
	 * @return
	 */
	public List<DocumentosPendentesVO> pesquisarPendentesePendentesResponsavelPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores responsavel, AipPacientes paciente) {

		// Obtem pendentes - tipo 2
		List<AghVersaoDocumento> listaPendentes = this
				.getAghVersaoDocumentoDAO().pesquisarPendentes(firstResult,
						maxResult, orderProperty, asc, responsavel, paciente);

		List<DocumentosPendentesVO> pendentes = popularListaDocumentosPendentes(listaPendentes);

		// Obtem pendentes responsavel - tipo 1
		List<AghVersaoDocumento> listaPendentesResponsavel = this
				.getAghVersaoDocumentoDAO()
				.pesquisarPendentesResponsavelPaciente(firstResult, maxResult,
						orderProperty, asc, responsavel, paciente);

		List<DocumentosPendentesVO> pendentesResponsavel = popularListaDocumentosPendentes(listaPendentesResponsavel);

		// Cria lista unica para retorno com elementos das duas listas, não
		// repetidos
		for (DocumentosPendentesVO pendente : pendentes) {
			if (!pendentesResponsavel.contains(pendente)) {
				pendentesResponsavel.add(pendente);
			}
		}

		return pendentesResponsavel;
	}
	
	/**
	 * Pesquisa os documentos pendentes do paciente e os documentos do
	 * responsavel do paciente em uma unica lista, utilizado na Visão:
	 * Documentos do Paciente na lista de pacientes
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param responsavel
	 * @param paciente
	 * @return
	 */
	public List<DocumentosPendentesVO> pesquisarPendentesPacienteePendentesResponsavelPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores responsavel, AipPacientes paciente) {

		// Obtem pendentes - tipo 3
		List<AghVersaoDocumento> listaPendentesPaciente = this
				.getAghVersaoDocumentoDAO().pesquisarPendentesPaciente(
						firstResult, maxResult, orderProperty, asc,
						responsavel, paciente);

		List<DocumentosPendentesVO> pendentesPaciente = popularListaDocumentosPendentes(listaPendentesPaciente);

		// Obtem pendentes responsavel - tipo 1
		List<AghVersaoDocumento> listaPendentesResponsavel = this
				.getAghVersaoDocumentoDAO()
				.pesquisarPendentesResponsavelPaciente(firstResult, maxResult,
						orderProperty, asc, responsavel, paciente);

		List<DocumentosPendentesVO> pendentesResponsavel = popularListaDocumentosPendentes(listaPendentesResponsavel);

		// Cria lista unica para retorno com elementos das duas listas, não
		// repetidos
		for (DocumentosPendentesVO pendentePaciente : pendentesPaciente) {
			if (!pendentesResponsavel.contains(pendentePaciente)) {
				pendentesResponsavel.add(pendentePaciente);
			}
		}

		return pendentesResponsavel;
	}

	/**
	 * Retorna o número de dias que o documento está pendente
	 * @param responsavel
	 * @return
	 */
	public Integer verificarNumDiasPendente(RapServidores responsavel) {

		Date dataDoc = this.getAghVersaoDocumentoDAO().verificarDocPendenteAntigo(
				responsavel);
		
		Integer dias = DateUtil.diffInDaysInteger(new Date(), dataDoc);

		return dias;
	}

	private DocumentosPendentesVO popularDocumentoPendente(AghVersaoDocumento aghVersaoDocumento) {
	
		DocumentosPendentesVO documentosPendentesVO = new DocumentosPendentesVO();
	
		documentosPendentesVO.setCriadoEm(aghVersaoDocumento.getCriadoEm());
		documentosPendentesVO.setDocumento(aghVersaoDocumento
				.getAghDocumentos().getTipo());
	
		if (aghVersaoDocumento.getAghDocumentos().getAghAtendimento() != null) {
			documentosPendentesVO.setNome(aghVersaoDocumento
					.getAghDocumentos().getAghAtendimento().getPaciente()
					.getNome());
		} else if (aghVersaoDocumento.getAghDocumentos().getCirurgia() != null) {
			documentosPendentesVO.setNome(aghVersaoDocumento
					.getAghDocumentos().getCirurgia().getPaciente()
					.getNome());
		} else if (aghVersaoDocumento.getAghDocumentos()
				.getFichaAnestesia() != null) {
			documentosPendentesVO.setNome(aghVersaoDocumento
					.getAghDocumentos().getFichaAnestesia().getPaciente()
					.getNome());
		} else if (aghVersaoDocumento.getAghDocumentos()
				.getNotaAdicionalEvolucao() != null) {
			documentosPendentesVO.setNome(aghVersaoDocumento
					.getAghDocumentos().getNotaAdicionalEvolucao()
					.getPaciente().getNome());
		} else if (aghVersaoDocumento.getAghDocumentos()
				.getLaudoAih() != null) {
			documentosPendentesVO.setNome(aghVersaoDocumento
					.getAghDocumentos().getLaudoAih()
					.getPaciente().getNome());
		} else if (aghVersaoDocumento.getAghDocumentos()
				.getAgenda() != null) {
			documentosPendentesVO.setNome(aghVersaoDocumento
					.getAghDocumentos().getAgenda()
					.getPaciente().getNome());
		}
	
		if (aghVersaoDocumento.getAghDocumentos().getAghAtendimento() != null) {
			documentosPendentesVO.setProntuario(aghVersaoDocumento
					.getAghDocumentos().getAghAtendimento().getPaciente()
					.getProntuario());
		} else if (aghVersaoDocumento.getAghDocumentos().getCirurgia() != null) {
			documentosPendentesVO.setProntuario(aghVersaoDocumento
					.getAghDocumentos().getCirurgia().getPaciente()
					.getProntuario());
		} else if (aghVersaoDocumento.getAghDocumentos()
				.getFichaAnestesia() != null) {
			documentosPendentesVO.setProntuario(aghVersaoDocumento
					.getAghDocumentos().getFichaAnestesia().getPaciente()
					.getProntuario());
		} else if (aghVersaoDocumento.getAghDocumentos()
				.getNotaAdicionalEvolucao() != null) {
			documentosPendentesVO.setProntuario(aghVersaoDocumento
					.getAghDocumentos().getNotaAdicionalEvolucao()
					.getPaciente().getProntuario());
		} else if (aghVersaoDocumento.getAghDocumentos()
				.getLaudoAih() != null) {
			documentosPendentesVO.setProntuario(aghVersaoDocumento
					.getAghDocumentos().getLaudoAih()
					.getPaciente().getProntuario());
		} else if (aghVersaoDocumento.getAghDocumentos()
				.getAgenda() != null) {
			documentosPendentesVO.setProntuario(aghVersaoDocumento
					.getAghDocumentos().getAgenda()
					.getPaciente().getProntuario());
		}
		
		
		documentosPendentesVO.setOriginal(aghVersaoDocumento.getOriginal());
	
		documentosPendentesVO.setResponsavel(aghVersaoDocumento
				.getServidorResp().getPessoaFisica().getNome());
		documentosPendentesVO.setSituacao(aghVersaoDocumento.getSituacao());
		documentosPendentesVO.setSeq(aghVersaoDocumento.getSeq());
		
		return documentosPendentesVO;
	}

	private List<DocumentosPendentesVO> popularListaDocumentosPendentes(
			List<AghVersaoDocumento> lista) {

		List<DocumentosPendentesVO> documentosPendentesVOList = new ArrayList<DocumentosPendentesVO>();
		DocumentosPendentesVO documentosPendentesVO = null;

		for (AghVersaoDocumento aghVersaoDocumento : lista) {
			documentosPendentesVO = this.popularDocumentoPendente(
					aghVersaoDocumento);

			documentosPendentesVOList.add(documentosPendentesVO);
		}

		return documentosPendentesVOList;
	}

	protected AghVersaoDocumentoDAO getAghVersaoDocumentoDAO() {
		return aghVersaoDocumentoDAO;
	}

}
