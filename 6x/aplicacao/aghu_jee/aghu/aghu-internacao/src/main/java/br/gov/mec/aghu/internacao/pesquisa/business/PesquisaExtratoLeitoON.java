package br.gov.mec.aghu.internacao.pesquisa.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ExtratoLeitoVO;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;

@Stateless
public class PesquisaExtratoLeitoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisaExtratoLeitoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinExtratoLeitosDAO ainExtratoLeitosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7748364095376303835L;

	@Secure("#{s:hasPermission('leito','pesquisarExtrato')}")
	public List<ExtratoLeitoVO> montarExtratoLeitoVO(String leito, Date data, Integer firstResult, Integer maxResult) {

		List<ExtratoLeitoVO> retorno = new ArrayList<ExtratoLeitoVO>();

		List<AinExtratoLeitos> extratos = getAinExtratoLeitosDAO().pesquisarExtratoLeitos(leito, data, firstResult, maxResult,
				AinExtratoLeitos.Fields.CRIADO_EM.toString(), false);

		// Criando lista de VO.
		Iterator<AinExtratoLeitos> it = extratos.iterator();

		while (it.hasNext()) {

			AinExtratoLeitos extrato = it.next();
			ExtratoLeitoVO extratoLeitoVO = new ExtratoLeitoVO();

			extratoLeitoVO.setCriadoEm(extrato.getCriadoEm());
			extratoLeitoVO.setTipoMovtoDescricao(extrato.getTipoMovimentoLeito().getDescricao().toUpperCase());
			extratoLeitoVO.setDthrLancamento(extrato.getDthrLancamento());

			if (extrato.getServidor() != null && extrato.getServidor().getId() != null
					&& extrato.getServidor().getPessoaFisica() != null && extrato.getServidor().getPessoaFisica().getCodigo() != null) {
				extratoLeitoVO.setServidorResponsalvel(extrato.getServidor().getPessoaFisica().getNome());
			}

			if (extrato.getTipoMovimentoLeito().getGrupoMvtoLeito().equals(DominioMovimentoLeito.O)) {
				if (extrato.getInternacao() != null && extrato.getInternacao().getSeq() != null) {
					extratoLeitoVO.setNome(extrato.getInternacao().getPaciente().getNome());
					extratoLeitoVO.setProntuario(extrato.getInternacao().getPaciente().getProntuario());
					extratoLeitoVO.setConvenioDescricao(extrato.getInternacao().getConvenioSaude().getDescricao());
				}

				if (extrato.getAtendimentoUrgencia() != null && extrato.getAtendimentoUrgencia().getSeq() != null) {
					extratoLeitoVO.setNome(extrato.getAtendimentoUrgencia().getPaciente().getNome());
					extratoLeitoVO.setProntuario(extrato.getAtendimentoUrgencia().getPaciente().getProntuario());
					extratoLeitoVO.setConvenioDescricao(extrato.getAtendimentoUrgencia().getConvenioSaude().getDescricao());
				}
			}
			retorno.add(extratoLeitoVO);
		}
		return retorno;
	}

	public Long pesquisarExtratoLeitoCount(String leito, Date data) {
		return getAinExtratoLeitosDAO().pesquisarExtratoLeitoCount(leito, data);
	}

	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO() {
		return ainExtratoLeitosDAO;
	}
}