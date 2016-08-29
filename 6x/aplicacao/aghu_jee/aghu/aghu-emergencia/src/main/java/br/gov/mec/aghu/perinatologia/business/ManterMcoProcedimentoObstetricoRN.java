package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoProcedimentoObstetricos;
import br.gov.mec.aghu.perinatologia.dao.McoProcedimentoObstetricosDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterMcoProcedimentoObstetricoRN extends BaseBusiness {

	private static final long serialVersionUID = -4431300112732051146L;

	@Inject
	private McoProcedimentoObstetricosDAO dao;
	
	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	enum ManterMcoProcedimentoObstetricoRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_ERRO_PROCEDIMENTO_OBSTETRICO_JA_CADASTRADO, MENSAGEM_ERRO_NAO_PERMITIDO_ALTERACAO_DESTE_CAMPO
	}

	@Override
	protected Log getLogger() {
		return null;
	}

	public void cadastrarProcedimento(McoProcedimentoObstetricos procedimento) throws ApplicationBusinessException {
		if (dao.existeMcoProcedimentoObstetricosPorDescricao(procedimento.getDescricao())) {
			throw new ApplicationBusinessException(
					ManterMcoProcedimentoObstetricoRNExceptionCode.MENSAGEM_ERRO_PROCEDIMENTO_OBSTETRICO_JA_CADASTRADO);
		}

		procedimento.setSerMatricula(usuario.getMatricula());
		procedimento.setSerVinCodigo(usuario.getVinculo());
		
		procedimento.setCriadoEm(new Date());

		dao.persistir(procedimento);
	}

	public void alterarProcedimento(McoProcedimentoObstetricos procedimento) throws ApplicationBusinessException{
		if (dao.descricaoFoiAlterada(procedimento.getSeq(),procedimento.getDescricao())) {
			throw new ApplicationBusinessException(
					ManterMcoProcedimentoObstetricoRNExceptionCode.MENSAGEM_ERRO_NAO_PERMITIDO_ALTERACAO_DESTE_CAMPO);
		}
		procedimento.setSerMatricula(usuario.getMatricula());
		procedimento.setSerVinCodigo(usuario.getVinculo());
		
		dao.atualizar(procedimento);
	}

	public List<McoProcedimentoObstetricos> pesquisarMcoProcedimentoObstetricos(Integer firstResult, Integer maxResults,String orderProperty,boolean asc,Short seq,String descricao,Integer codigoPHI,DominioSituacao dominioSituacao) {
		orderProperty = (orderProperty == null ? McoProcedimentoObstetricos.Fields.DESCRICAO.toString(): orderProperty);
		return dao.pesquisarMcoProcedimentoObstetricos(firstResult, maxResults,orderProperty,asc,seq,descricao,codigoPHI,dominioSituacao);
	}

	public void ativarDesativarProcedimentoObstetrico(McoProcedimentoObstetricos procedimentoObstetrico) {
		dao.atualizar(procedimentoObstetrico);
	}

	public Long pesquisarMcoProcedimentoObstetricosCount(Short seq,
			String descricao, Integer codigoPHI, DominioSituacao dominioSituacao) {
		 
		return dao.pesquisarMcoProcedimentoObstetricosCount(seq,descricao, codigoPHI,dominioSituacao);
	}
}
