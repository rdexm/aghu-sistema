package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.EditaNiveisAlcadaAprovacaoRN.EditaNiveisAlcadaAprovacaoRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAlcadaAvalOpmsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAlcadaAvalOpmsJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoAlcadaAvalOpmsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpmsJn;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PesquisaNiveisAlcadaAprovacaoRN  extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PesquisaNiveisAlcadaAprovacaoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private MbcAlcadaAvalOpmsDAO mbcAlcadaAvalOpmsDAO;

	@Inject
	private MbcAlcadaAvalOpmsJnDAO mbcAlcadaAvalOpmsJnDAO;
	
	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@Inject
	private MbcGrupoAlcadaAvalOpmsDAO mbcGrupoAlcadaAvalOpmsDAO;
	
	private static final long serialVersionUID = 5334901747730952415L;

	public enum PesquisaNiveisAlcadaAprovacaoRNExceptionCode implements BusinessExceptionCode {
		ERRO_CADASTRO_NIVEL_ALCADA_VALORES_INCORRETOS, ERRO_CADASTRO_NIVEL_ALCADA_FAIXA_VALORES, ERRO_CADASTRO_NIVEL_ALCADA_CODIGO_DO_GRUPO_NAO_INFORMADO;
	}

	
	public List<MbcAlcadaAvalOpms> buscaNiveisAlcadaAprovacaoPorGrupo(Short seq) throws ApplicationBusinessException  {
		if(seq == null || seq.equals(0)) {
			throw new ApplicationBusinessException(PesquisaNiveisAlcadaAprovacaoRNExceptionCode.ERRO_CADASTRO_NIVEL_ALCADA_CODIGO_DO_GRUPO_NAO_INFORMADO);
		}
		return getMbcAlcadaAvalOpmsDAO().buscaNiveisAlcadaAprovacaoPorGrupo(seq);
	}
	
	public List<MbcAlcadaAvalOpms> buscaNiveisAlcadaAprovacaoPorGrupoValor(Short seq) throws ApplicationBusinessException  {
		return getMbcAlcadaAvalOpmsDAO().buscaNiveisAlcadaAprovacaoPorGrupoValor(seq);
	}

	private MbcAlcadaAvalOpmsDAO getMbcAlcadaAvalOpmsDAO() {
		return mbcAlcadaAvalOpmsDAO;
	}

	public void persistirNivelAlcada(MbcAlcadaAvalOpms nivelAlcada) throws ApplicationBusinessException {
		
		// verifica se existem outras alcadas e se alguma ja foi utilizada
		// anteriormente
		
		
		if (nivelAlcada.getGrupoAlcada() != null) {
			
			List<MbcGrupoAlcadaAvalOpms> listaGrupoAlcadaAvalOpms = mbcGrupoAlcadaAvalOpmsDAO.retornaGruoAlcadaAvalPorGrupoSeq(nivelAlcada.getGrupoAlcada().getSeq());

			//for (MbcAlcadaAvalOpms alcadaExistente : listaGrupoAlcadaAvalOpms.getAlcadas()) {
			for(MbcGrupoAlcadaAvalOpms listaGrupo : listaGrupoAlcadaAvalOpms){
				for (MbcAlcadaAvalOpms alcadaExistente : listaGrupo.getAlcadas()) {
					if (alcadaExistente.getSeq() != null) {
						List<MbcRequisicaoOpmes> requisicoesExistentes = this.mbcRequisicaoOpmesDAO.
								obterRequisicaoPorGrupoAlcadaSeq(alcadaExistente.getSeq());
					
						if (requisicoesExistentes != null && !requisicoesExistentes.isEmpty()) {
							throw new ApplicationBusinessException(EditaNiveisAlcadaAprovacaoRNExceptionCode.
									MENSAGEM_ERRO_NIVEL_AVAL_OPME_JA_UTILIZADO);
						}
					}
				}
			}
		}
		
		if(nivelAlcada.getValorMinimo() == null ||
		   nivelAlcada.getValorMaximo() == null || 
		   nivelAlcada.getDescricao() == null) {
			throw new ApplicationBusinessException(PesquisaNiveisAlcadaAprovacaoRNExceptionCode.
					ERRO_CADASTRO_NIVEL_ALCADA_VALORES_INCORRETOS);
		}
		
		if(existeFaixaDeValoresDaAlcada(nivelAlcada)) {
			throw new ApplicationBusinessException(PesquisaNiveisAlcadaAprovacaoRNExceptionCode.
					ERRO_CADASTRO_NIVEL_ALCADA_FAIXA_VALORES);
		}
		
		List<MbcAlcadaAvalOpms> listaNiveis = buscaNiveisAlcadaAprovacaoPorGrupo(
																	nivelAlcada.getGrupoAlcada().getSeq());
		Integer numeroNivelAlcada = 1;
		
		if (listaNiveis.size() > 0) {
			numeroNivelAlcada = listaNiveis.get(listaNiveis.size()-1).getNivelAlcada() + 1;
		}
		nivelAlcada.setNivelAlcada(numeroNivelAlcada);
		getMbcAlcadaAvalOpmsDAO().persistir(nivelAlcada);
		
		posInserirMbcAlcadaAvalOpms(nivelAlcada);
	}
	
	protected void posInserirMbcAlcadaAvalOpms(MbcAlcadaAvalOpms alcadaAvalOpms) {
		inserirJournal(alcadaAvalOpms, DominioOperacoesJournal.INS);
	}
	
	protected void inserirJournal(MbcAlcadaAvalOpms elemento, DominioOperacoesJournal operacao) {
		MbcAlcadaAvalOpmsJn journal = BaseJournalFactory.getBaseJournal(operacao,	MbcAlcadaAvalOpmsJn.class, obterLoginUsuarioLogado());

		journal.setSeq(elemento.getSeq());
		journal.setGrupoAlcada(elemento.getGrupoAlcada());
		journal.setNivelAlcada(elemento.getNivelAlcada());
		journal.setDescricao(elemento.getDescricao());
		journal.setValorMinimo(elemento.getValorMinimo());
		journal.setValorMaximo(elemento.getValorMaximo());
		journal.setCriadoEm(elemento.getCriadoEm());
		journal.setModificadoEm(elemento.getModificadoEm());
		journal.setRapServidores(elemento.getRapServidores());
		journal.setRapServidoresModificacao(elemento.getRapServidoresModificacao());
		
		this.mbcAlcadaAvalOpmsJnDAO.persistir(journal);
	}

//	private int buscaNumeroAlcadasGrupo(Short codigoGrupo) {
//		return getMbcAlcadaAvalOpmsDAO().buscaNiveisAlcadaAprovacaoPorGrupoCount(codigoGrupo);
//	}

	private boolean existeFaixaDeValoresDaAlcada(MbcAlcadaAvalOpms nivelAlcada) {
		Short codigoGrupo = nivelAlcada.getGrupoAlcada().getSeq();
		boolean existeValorMinimo = getMbcAlcadaAvalOpmsDAO().verificaExistenciaValorNivelAlcada(codigoGrupo, nivelAlcada.getValorMinimo());
		boolean existeValorMaximo = getMbcAlcadaAvalOpmsDAO().verificaExistenciaValorNivelAlcada(codigoGrupo, nivelAlcada.getValorMaximo());
		return existeValorMinimo || existeValorMaximo;
	}
}
