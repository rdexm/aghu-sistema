package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.perinatologia.dao.McoCondutaDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterCadastroCondutaRN extends BaseBusiness {
	private static final long serialVersionUID = -3762844367738161125L;

	@Inject
	McoCondutaDAO mcoCondutaDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private RapServidoresDAO servidorDAO;

	private enum ManterCadastroCondutaRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_CONDUTA_JA_CADASTRADA, MENSAGEM_ERRO_NAO_PERMITIDO_ALTERACAO_DESTE_CAMPO
	}

	public List<McoConduta> listarCondutas(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Long codigo,
			String descricao, Integer faturamento, DominioSituacao situacao) {
		return mcoCondutaDAO.listarCondutas(firstResult, maxResults, orderProperty, asc, codigo, descricao, faturamento, situacao);
	}

	public Long listarCondutasCount(Long codigo, String descricao, Integer faturamento, DominioSituacao situacao) {
		return mcoCondutaDAO.listarCondutasCount(codigo, descricao, faturamento, situacao);
	}

	public void persistirConduta(McoConduta conduta) throws ApplicationBusinessException {

		if (conduta != null && conduta.getSeq() == null) {
			if (!this.pesquisaCondutaExistente(conduta.getDescricao())) {
				conduta.setCriadoEm(new Date());				
				conduta.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
				
				this.mcoCondutaDAO.persistir(conduta);
			} else {
				throw new ApplicationBusinessException(ManterCadastroCondutaRNExceptionCode.MENSAGEM_ERRO_CONDUTA_JA_CADASTRADA);
			}
		} else {
			if (conduta != null) {
				McoConduta condutaDB = this.mcoCondutaDAO.obterOriginal(conduta.getSeq());

				if (condutaDB.getRapServidores().getId().getMatricula()
						.equals(usuario.getMatricula())
						&& condutaDB.getRapServidores().getId().getVinCodigo()
								.equals(usuario.getVinculo())) {

					conduta.setRapServidores(servidorDAO
							.obter(new RapServidoresId(usuario.getMatricula(),
									usuario.getVinculo())));
				}

				if (!conduta.getDescricao().equalsIgnoreCase(condutaDB.getDescricao())) {
					throw new ApplicationBusinessException(ManterCadastroCondutaRNExceptionCode.MENSAGEM_ERRO_NAO_PERMITIDO_ALTERACAO_DESTE_CAMPO);
				}

				this.mcoCondutaDAO.atualizar(conduta);
			}
		}
	}

	public void ativarInativarConduta(McoConduta conduta) {
		if(conduta.getIndSituacao().equals(DominioSituacao.A)){
			conduta.setIndSituacao(DominioSituacao.I);
		}
		else {
			conduta.setIndSituacao(DominioSituacao.A);
		}		
		
		conduta.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		mcoCondutaDAO.atualizar(conduta);
	}

	private Boolean pesquisaCondutaExistente(String descricao) {
		return this.mcoCondutaDAO.pesquisaCondutaExistente(descricao);
	}

	@Override
	protected Log getLogger() {
		return null;
	}

}